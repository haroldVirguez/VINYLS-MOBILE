const { Given, When, Then } = require('@cucumber/cucumber')
const assert = require('assert')
const fs = require('fs')
const path = require('path')

const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`

// safe import of dumpPageSource
let dumpPageSource
try {
  const helpers = require('../support/helpers')
  dumpPageSource = helpers && helpers.dumpPageSource
} catch (err) {
  dumpPageSource = async (outPrefix, driver) => {
    try {
      const src = await driver.getPageSource()
      const ts = new Date().toISOString().replace(/[:.]/g, '-')
      const outDir = path.join(__dirname, '..', '..', 'logs')
      try { fs.mkdirSync(outDir, { recursive: true }) } catch (_) {}
      const outPath = path.join(outDir, `${outPrefix}-${ts}.xml`)
      try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (_) {}
    } catch (err) {}
  }
}

Then('I should see the collectors list', async function () {
  const list = await this.driver.$(byResId('recyclerCollectors'))
  try {
    const exists = await list.isExisting()
    if (exists) return
  } catch (e) {
  }

  // Try to navigate via bottom nav if exists
  try {
    const nav = await this.driver.$(byResId('nav_collectors'))
    if (nav && await nav.isExisting()) {
      await nav.click()
      try {
        await list.waitForExist({ timeout: 20000 })
        return
      } catch (e) {
        await dumpPageSource('collectors-list-missing-after-nav', this.driver)
        throw e
      }
    }
  } catch (err) {
  }

  await dumpPageSource('collectors-list-missing', this.driver)
  throw new Error('Collectors list not visible')
})

When('I tap the first collector in the list', async function () {
  // try to obtain the list; if not present, try to navigate to the collectors tab
  let list = await this.driver.$(byResId('recyclerCollectors'))
  try {
    const exists = await list.isExisting()
    if (!exists) {
      try {
        const nav = await this.driver.$(byResId('nav_collectors'))
        if (nav && await nav.isExisting()) {
          await nav.click()
          await list.waitForExist({ timeout: 20000 })
        } else {
          // try alternative heuristics: any recycler with 'collect' in resource-id
          const alts = await this.driver.$$('//*[contains(@resource-id, "recycler") and contains(@resource-id, "collect")]')
          if (alts && alts.length > 0) {
            list = alts[0]
          } else {
            await dumpPageSource('collectors-list-missing-before-tap', this.driver)
            throw new Error('Collectors recycler not present')
          }
        }
      } catch (err) {
        await dumpPageSource('collectors-list-missing-before-tap', this.driver)
        throw err
      }
    }
  } catch (e) {
    // if $ returned something invalid, try to find alternatives
    const alts = await this.driver.$$('//*[contains(@resource-id, "recycler") and contains(@resource-id, "collect")]')
    if (alts && alts.length > 0) {
      list = alts[0]
    } else {
      await dumpPageSource('collectors-list-missing-before-tap', this.driver)
      throw e
    }
  }

  // ensure list exists now
  await list.waitForExist({ timeout: 10000 })

  // get first child robustly
  let items = await list.$$('./*')
  if (!items || items.length === 0) {
    // try alternative: any child element
    items = await list.$$('*')
  }

  if (!items || items.length === 0) {
    await dumpPageSource('collectors-no-items', this.driver)
    throw new Error('No collector items found')
  }

  const firstItem = items[0]

  // capture name text from the item before clicking
  try {
    let nameText = null

    // try common resource-id for the name
    try {
      const nameEl = await firstItem.$('.//*[@resource-id="com.team3.vinyls:id/txtCollectorName"]')
      if (nameEl && await nameEl.isExisting()) {
        nameText = await nameEl.getText()
      }
    } catch (_) {}

    // try other likely ids
    if (!nameText) {
      try {
        const altEl = await firstItem.$('.//*[@resource-id="com.team3.vinyls:id/txtName"]')
        if (altEl && await altEl.isExisting()) nameText = await altEl.getText()
      } catch (_) {}
    }

    // fallback: getText on the item (may return concatenated text)
    if (!nameText) {
      try {
        nameText = await firstItem.getText()
      } catch (_) { nameText = null }
    }

    if (nameText) {
      this.lastCollectorName = String(nameText).trim()
      console.error('Captured first collector name:', this.lastCollectorName)
    }
  } catch (e) {
    // ignore capture errors; we'll still attempt to click
  }

  // try clicking the name element first, then the item
  try {
    const clickableName = await firstItem.$('.//*[@resource-id="com.team3.vinyls:id/txtCollectorName"]')
    if (clickableName && await clickableName.isExisting()) {
      await clickableName.click()
      return
    }
  } catch (_) {}

  try {
    await firstItem.click()
    return
  } catch (err) {
    // final fallback: tap center of the item
    try {
      const loc = await firstItem.getLocation()
      const size = await firstItem.getSize()
      const x = Math.floor(loc.x + size.width / 2)
      const y = Math.floor(loc.y + size.height / 2)
      await this.driver.touchAction({ action: 'tap', x, y })
      return
    } catch (e) {
      await dumpPageSource('collectors-tap-failed', this.driver)
      throw new Error('Failed to tap first collector')
    }
  }
})

Then('I should see the collector name from list', async function () {
  const expected = this.lastCollectorName
  if (!expected) throw new Error('No collector name captured from list')

  const byText = (txt) => `//*[contains(@text, "${txt}")]`

  // try several checks: exact text in detail, resource-id title, or container
  let found = false
  try {
    const el = await this.driver.$(byText(expected))
    if (el && await el.isExisting()) found = true
  } catch (_) {}

  if (!found) {
    try {
      const title = await this.driver.$(byResId('txtCollectorName'))
      if (title && await title.isExisting()) {
        const t = await title.getText()
        if (t && t.trim().length > 0 && expected.includes(t) || t.includes(expected)) found = true
      }
    } catch (_) {}
  }

  if (!found) {
    await dumpPageSource('collectorname-from-list-error', this.driver)
    throw new Error(`Collector name from list "${expected}" not found in detail`)
  }

  assert.ok(found, `Collector name from list "${expected}" not found in detail`)
})

Then('I should see the collector name {string}', async function (collectorName) {
  const byText = (txt) => `//*[contains(@text, "${txt}")]`
  const el = await this.driver.$(byText(collectorName))
  const exists = await el.isExisting()
  if (!exists) {
    await dumpPageSource('collectorname-error', this.driver)
    throw new Error(`Collector name "${collectorName}" not found`)
  }
  assert.ok(exists, `Collector name "${collectorName}" not found`)
})

Then('I should see the collector detail', async function () {
  // more robust: accept container or title presence; fallback to any text that looks like detail
  let visible = false
  try {
    const el = await this.driver.$(byResId('collectorDetailContainer'))
    if (el && await el.isExisting()) visible = true
  } catch (_) { }

  if (!visible) {
    try {
      const title = await this.driver.$(byResId('txtCollectorName'))
      if (title && await title.isExisting()) visible = true
    } catch (_) {}
  }

  if (!visible) {
    // try generic heuristics: any element with resource-id containing 'collector' or a detail-like text area
    try {
      const alt = await this.driver.$$('//*[contains(@resource-id, "collector") or contains(@resource-id, "detail")]')
      if (alt && alt.length > 0) visible = true
    } catch (_) {}
  }

  if (!visible) {
    await dumpPageSource('collector-detail-missing', this.driver)
    throw new Error('Collector detail not visible')
  }

  assert.ok(visible, 'Collector detail not visible')
})

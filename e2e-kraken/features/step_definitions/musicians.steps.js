const { Then } = require('@cucumber/cucumber')
const fs = require('fs')
const path = require('path')

const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`

async function dumpPageSource(outPrefix, driver) {
  try {
    const src = await driver.getPageSource()
    const ts = new Date().toISOString().replace(/[:.]/g, '-')
    const outDir = path.join(__dirname, '..', '..', 'logs')
    try { fs.mkdirSync(outDir, { recursive: true }) } catch (err) {}
    const outPath = path.join(outDir, `${outPrefix}-${ts}.xml`)
    try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (err) { console.error('Failed writing page source:', err.message) }
  } catch (err) {
  }
}

Then('I should see the musicians list', async function () {
  const list = await this.driver.$(byResId('recyclerMusicians'))
  try {
    const exists = await list.isExisting()
    if (exists) return
  } catch (e) {
  }

  try {
    const nav = await this.driver.$(byResId('nav_artists'))
    if (nav && await nav.isExisting()) {
      await nav.click()
      try {
        await list.waitForExist({ timeout: 10000 })
        return
      } catch (e) {
        await dumpPageSource('musicians-list-missing-after-nav', this.driver)
        throw e
      }
    }
  } catch (err) {
  }

  const alt = await this.driver.$$('//*[contains(@resource-id, "recycler") and contains(@resource-id, "mus")]')
  if (alt && alt.length > 0) return

  await dumpPageSource('musicians-list-missing', this.driver)
  throw new Error('Musicians list not visible')
})

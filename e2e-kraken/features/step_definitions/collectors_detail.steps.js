const { Then } = require('@cucumber/cucumber')
const assert = require('assert')
const fs = require('fs')
const path = require('path')

const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`

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

Then('I should see the collector avatar', async function () {
  const avatar = await this.driver.$(byResId('imgCollectorAvatar'))
  
  try {
    await avatar.waitForExist({ timeout: 5000 })
    const exists = await avatar.isExisting()
    
    if (!exists) {
      await dumpPageSource('collector-avatar-missing', this.driver)
      throw new Error('Collector avatar not visible')
    }
    
    assert.ok(exists, 'Collector avatar not visible')
  } catch (e) {
    await dumpPageSource('collector-avatar-error', this.driver)
    throw e
  }
})

Then('I should see the collector contact information', async function () {
  try {
    await this.driver.$(
      'android=new UiScrollable(new UiSelector().scrollable(true))' +
      '.scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/txtTelephoneValue"))'
    )
  } catch (_) { }

  let telephoneVisible = false
  try {
    const telephoneEl = await this.driver.$(byResId('txtTelephoneValue'))
    if (telephoneEl && await telephoneEl.isExisting()) {
      const text = await telephoneEl.getText()
      if (text && text.trim().length > 0) {
        telephoneVisible = true
      }
    }
  } catch (_) {}

  let emailVisible = false
  try {
    const emailEl = await this.driver.$(byResId('txtEmailValue'))
    if (emailEl && await emailEl.isExisting()) {
      const text = await emailEl.getText()
      if (text && text.trim().length > 0) {
        emailVisible = true
      }
    }
  } catch (_) {}

  if (!telephoneVisible && !emailVisible) {
    await dumpPageSource('collector-contact-info-missing', this.driver)
    throw new Error('Collector contact information (telephone or email) not visible')
  }

  assert.ok(telephoneVisible || emailVisible, 'Collector contact information not visible')
})

Then('I should see the collector comments section', async function () {
  try {
    await this.driver.$(
      'android=new UiScrollable(new UiSelector().scrollable(true))' +
      '.scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/commentsContainer"))'
    )
  } catch (_) { }

  const commentsContainer = await this.driver.$(byResId('commentsContainer'))
  
  try {
    await commentsContainer.waitForExist({ timeout: 5000 })
    const exists = await commentsContainer.isExisting()
    
    if (!exists) {
      await dumpPageSource('collector-comments-section-missing', this.driver)
      throw new Error('Collector comments section not visible')
    }
    
    const children = await commentsContainer.$$('./*')
    const hasComments = children && children.length > 0
    
    assert.ok(exists, 'Collector comments section not visible')
  } catch (e) {
    await dumpPageSource('collector-comments-section-error', this.driver)
    throw e
  }
})

Then('I should see the collector favorite performers section', async function () {
  try {
    await this.driver.$(
      'android=new UiScrollable(new UiSelector().scrollable(true))' +
      '.scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/performersContainer"))'
    )
  } catch (_) { }

  const performersContainer = await this.driver.$(byResId('performersContainer'))
  
  try {
    await performersContainer.waitForExist({ timeout: 5000 })
    const exists = await performersContainer.isExisting()
    
    if (!exists) {
      await dumpPageSource('collector-performers-section-missing', this.driver)
      throw new Error('Collector favorite performers section not visible')
    }
    
    assert.ok(exists, 'Collector favorite performers section not visible')
  } catch (e) {
    await dumpPageSource('collector-performers-section-error', this.driver)
    throw e
  }
})


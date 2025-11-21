const fs = require('fs')
const path = require('path')

async function dumpPageSource(outPrefix, driver) {
  try {
    const src = await driver.getPageSource()
    const ts = new Date().toISOString().replace(/[:.]/g, '-')
    const outDir = path.join(__dirname, '..', '..', 'logs')
    try { fs.mkdirSync(outDir, { recursive: true }) } catch (err) {}
    const outPath = path.join(outDir, `${outPrefix}-${ts}.xml`)
    try { fs.writeFileSync(outPath, src, 'utf8'); console.error(`Wrote page source to ${outPath}`) } catch (err) { console.error('Failed writing page source:', err.message) }
  } catch (err) {
    // ignore
  }
}

module.exports = { dumpPageSource }


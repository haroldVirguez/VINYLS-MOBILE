const { spawn } = require('child_process')
const path = require('path')
const fs = require('fs')

// parse args
const args = process.argv.slice(2)
const installFlag = args.includes('--install')

// ensure env variables for the test
process.env.E2E_APP_FLAVOR = process.env.E2E_APP_FLAVOR || 'prod'
process.env.E2E_INSTALL_APP = process.env.E2E_INSTALL_APP || (installFlag ? '1' : '0')

let cucumberBin = null
let useNpx = false

// 1) try require.resolve
try {
  cucumberBin = require.resolve('@cucumber/cucumber/bin/cucumber-js')
} catch (e) {
  // 2) try direct path relative to this script (scripts/ -> ../node_modules/...)
  try {
    const candidate = path.resolve(__dirname, '..', 'node_modules', '@cucumber', 'cucumber', 'bin', 'cucumber-js')
    fs.accessSync(candidate, fs.constants.X_OK | fs.constants.R_OK)
    cucumberBin = candidate
  } catch (e2) {
    // 3) fallback to npx
    console.warn('Falling back to npx cucumber-js because local bin not found')
    useNpx = true
  }
}

const node = process.execPath
const cucumberArgs = ['--require', 'features/step_definitions/**/*.js', '--require', 'features/support/**/*.js']

console.log('Ejecutando pruebas E2E con variables: ', {
  E2E_APP_FLAVOR: process.env.E2E_APP_FLAVOR,
  E2E_INSTALL_APP: process.env.E2E_INSTALL_APP,
  cucumberBin: cucumberBin,
  useNpx: useNpx
})

let child
if (useNpx) {
  // spawn npx cucumber-js ...
  const spawnCmd = process.platform === 'win32' ? 'npx.cmd' : 'npx'
  child = spawn(spawnCmd, ['cucumber-js', ...cucumberArgs], { stdio: 'inherit', env: process.env })
} else {
  // run node <cucumberBin> args
  child = spawn(node, [cucumberBin, ...cucumberArgs], { stdio: 'inherit', env: process.env })
}

child.on('exit', code => process.exit(code))
child.on('error', err => {
  console.error('Error al lanzar cucumber-js:', err)
  process.exit(1)
})

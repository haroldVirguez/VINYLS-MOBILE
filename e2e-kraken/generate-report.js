const reporter = require('cucumber-html-reporter');

const options = {
  theme: 'bootstrap',
  jsonFile: 'reports/cucumber_report.json',
  output: 'reports/index.html',
  reportSuiteAsScenarios: true,
  launchReport: true,
  metadata: {
    "Device": "Android Emulator",
    "Platform": "Android",
    "App": "Vinyls",
    "Generated": new Date().toLocaleString()
  }

};

reporter.generate(options);


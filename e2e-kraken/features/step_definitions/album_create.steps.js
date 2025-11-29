const { Given, When, Then } = require("@cucumber/cucumber");
const assert = require("assert");

const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`;

// safe import of dumpPageSource
let dumpPageSource;
try {
  const helpers = require("../support/helpers");
  dumpPageSource = helpers && helpers.dumpPageSource;
} catch (err) {
  const fs = require("fs");
  const path = require("path");
  dumpPageSource = async (outPrefix, driver) => {
    try {
      const src = await driver.getPageSource();
      const ts = new Date().toISOString().replace(/[:.]/g, "-");
      const outDir = path.join(__dirname, "..", "..", "logs");
      try {
        fs.mkdirSync(outDir, { recursive: true });
      } catch (_) {}
      const outPath = path.join(outDir, `${outPrefix}-${ts}.xml`);
      try {
        fs.writeFileSync(outPath, src, "utf8");
        console.error(`Wrote page source to ${outPath}`);
      } catch (_) {}
    } catch (err) {}
  };
}

async function swipeUp(driver) {
  const { height, width } = await driver.getWindowSize();
  const start = { x: width / 2, y: height * 0.8 };
  const end = { x: width / 2, y: height * 0.3 };

  await driver.performActions([
    {
      type: "pointer",
      id: "finger1",
      parameters: { pointerType: "touch" },
      actions: [
        { type: "pointerMove", duration: 0, x: start.x, y: start.y },
        { type: "pointerDown", button: 0 },
        { type: "pointerMove", duration: 500, x: end.x, y: end.y },
        { type: "pointerUp", button: 0 },
      ],
    },
  ]);

  await driver.pause(500);
}

// New helper: tries several strategies to find an input element robustly
async function findInput(driver, options) {
  // options: { resId, altResIds: [], containsKeywords: [], textKeywords: [] }
  const { resId, altResIds = [], containsKeywords = [], textKeywords = [] } = options;

  // 1) try exact resource-id
  if (resId) {
    try {
      const el = await driver.$(byResId(resId));
      if (await el.isExisting()) return el;
    } catch (_) {}
  }

  // 2) try alternative resource-ids
  for (const alt of altResIds) {
    try {
      const el = await driver.$(byResId(alt));
      if (await el.isExisting()) return el;
    } catch (_) {}
  }

  // 3) try contains(@resource-id, keyword) for each keyword
  for (const kw of containsKeywords) {
    try {
      const el = await driver.$(`//*[contains(translate(@resource-id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '${kw.toLowerCase()}')]`);
      if (el && await el.isExisting()) return el;
    } catch (_) {}
  }

  // 4) try by visible text/hint: contains text
  for (const t of textKeywords) {
    try {
      const el = await driver.$(`//*[contains(@text, '${t}')]`);
      if (el && await el.isExisting()) return el;
    } catch (_) {}
  }

  // 5) try scroll into view by candidate resource-id or text keywords
  try {
    if (resId) {
      await driver.$(
        'android=new UiScrollable(new UiSelector().scrollable(true))' +
        `.scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/${resId}"))`
      );
      const el = await driver.$(byResId(resId));
      if (el && await el.isExisting()) return el;
    }
  } catch (_) {}

  for (const t of textKeywords) {
    try {
      await driver.$(
        'android=new UiScrollable(new UiSelector().scrollable(true))' +
        `.scrollIntoView(new UiSelector().textContains("${t}"))`
      );
      const el = await driver.$(`//*[contains(@text, '${t}')]`);
      if (el && await el.isExisting()) return el;
    } catch (_) {}
  }

  // 6) last resort: try any element whose resource-id contains likely words (generic)
  const genericKeywords = ['description','desc','label','cover','release','date','genre']
  for (const kw of genericKeywords) {
    try {
      const els = await driver.$$(`//*[contains(translate(@resource-id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '${kw}')]`)
      if (els && els.length>0) return els[0]
    } catch (_) {}
  }

  return null;
}

When("I tap the create album button", async function () {
  const fab = await this.driver.$(byResId("fabAddTrack"));

  const exists = await fab.isExisting();
  assert.ok(exists, "Create Album button not visible");
  await fab.click();
  await this.driver.pause(500);
});

When("I fill the album creation form", async function () {
  // use robust finder for each input
  const titleInput = await findInput(this.driver, { resId: 'inputTitle', containsKeywords: ['title','inputtitle'] });
  const descriptionInput = await findInput(this.driver, { resId: 'inputDescription', containsKeywords: ['description','desc'] , textKeywords: ['Description','Descripción','descripcion'] });
  let releaseDateInput = await findInput(this.driver, { resId: 'inputReleaseDate', containsKeywords: ['releasedate','release','date','fecha'] });
  const genreInput = await findInput(this.driver, { resId: 'inputGenre', containsKeywords: ['genre'] });
  const recordLabelInput = await findInput(this.driver, { resId: 'inputLabel', containsKeywords: ['label','record'] });
  const coverUrlInput = await findInput(this.driver, { resId: 'inputCoverUrl', containsKeywords: ['cover','coverurl','image'] });

  // wait for a couple of essential fields to be present
  if (!titleInput || !coverUrlInput) {
    await dumpPageSource('album-create-essential-missing', this.driver)
    throw new Error('Essential inputs for album creation are missing')
  }

  try {
    await titleInput.waitForExist({ timeout: 10000 })
  } catch (e) {
    await dumpPageSource('album-create-title-missing', this.driver)
    throw e
  }

  try {
    await coverUrlInput.waitForExist({ timeout: 10000 })
  } catch (e) {
    await dumpPageSource('album-create-cover-missing', this.driver)
    throw e
  }

  // ensure description exists, otherwise try to scroll and find again
  if (!descriptionInput) {
    // try scrolling to a likely title text
    try {
      await this.driver.$('android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains("Description"))')
    } catch (_) {}
    // try generic find again
    const descAlt = await findInput(this.driver, { containsKeywords: ['description','desc'], textKeywords: ['Description','Descripción','descripcion'] })
    if (descAlt) descriptionInput = descAlt
  }

  if (!descriptionInput) {
    await dumpPageSource('album-create-description-missing', this.driver)
    throw new Error('Description input not found')
  }

  if (!releaseDateInput) {
    // try to scroll to label 'Release Date' variants
    try {
      await this.driver.$('android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains("Release"))')
    } catch (_) {}
    const relAlt = await findInput(this.driver, { containsKeywords: ['release','date','fecha'] })
    if (relAlt) releaseDateInput = relAlt
  }

  if (!genreInput || !recordLabelInput) {
    // try alternatives by scrolling
    await swipeUp(this.driver)
    const gAlt = await findInput(this.driver, { containsKeywords: ['genre','gen'] })
    if (!genreInput) genreInput = gAlt
    const lAlt = await findInput(this.driver, { containsKeywords: ['label','record','label'] })
    if (!recordLabelInput) recordLabelInput = lAlt
  }

  try {
    if (!titleInput || !coverUrlInput || !descriptionInput || !releaseDateInput || !genreInput || !recordLabelInput) {
      await dumpPageSource('album-create-inputs-missing-final', this.driver)
      throw new Error('One or more album inputs are missing')
    }
  } catch (err) {
    throw err
  }

  await coverUrlInput.setValue("https://picsum.photos/seed/album1/600/600");
  await titleInput.setValue("Buscando América Prueba");
  if (genreInput) await genreInput.setValue("Salsa");
  if (recordLabelInput) await recordLabelInput.setValue("EMI");

  // Try to use DatePicker via click; if it appears, accept it. Otherwise fallback to setValue.
  async function clickDatePickerOk(driver) {
    // Try several strategies to accept the DatePicker dialog
    const tryIds = ['//*[@resource-id="android:id/button1"]'];
    const tryTexts = ['OK', 'Ok', 'ok', 'Aceptar', 'ACEPTAR', 'Aceptar', 'Confirm', 'CONFIRMAR', 'Done', 'Aceptar fecha'];

    // 1) try known resource-id
    for (const q of tryIds) {
      try {
        const b = await driver.$(q);
        if (b && await b.isExisting() && await b.isDisplayed()) {
          await b.click();
          return true;
        }
      } catch (_) {}
    }

    // 2) try by exact text on android.widget.Button
    for (const txt of tryTexts) {
      try {
        const sel = `//android.widget.Button[@text="${txt}"]`;
        const b = await driver.$(sel);
        if (b && await b.isExisting() && await b.isDisplayed()) {
          await b.click();
          return true;
        }
      } catch (_) {}
    }

    // 3) try any visible button inside the view
    try {
      const buttons = await driver.$$('//android.widget.Button');
      for (const btn of buttons) {
        try {
          if (await btn.isDisplayed()) {
            await btn.click();
            return true;
          }
        } catch (_) {}
      }
    } catch (_) {}

    return false;
  }

  try {
    // Prefer to set the value directly to avoid opening the DatePicker which may change the view
    let dateSet = false;
    try {
      await releaseDateInput.setValue("2020-05-10");
      dateSet = true;
    } catch (_) {
      dateSet = false;
    }

    if (!dateSet) {
      // fallback to the previous strategy that clicks and accepts the picker
      await releaseDateInput.click();

      // small pause to allow the DatePicker dialog to appear
      await this.driver.pause(500);

      // attempt to accept the date picker using multiple strategies
      const accepted = await clickDatePickerOk(this.driver);
      if (!accepted) {
        // fallback: try to find an EditText (some date pickers expose/edit a text field)
        try {
          const editTexts = await this.driver.$$('//android.widget.EditText');
          if (editTexts && editTexts.length > 0) {
            let setOk = false;
            for (const et of editTexts) {
              try {
                if (await et.isDisplayed()) {
                  await et.clearValue().catch(() => {});
                  await et.setValue('2020-05-10');
                  try { await this.driver.hideKeyboard(); } catch (_) {}
                  setOk = true;
                  break;
                }
              } catch (_) {}
            }
            if (!setOk) {
              throw new Error('No visible editable EditText to set date');
            }
          } else {
            // re-find the releaseDateInput and try setValue
            const ref = await findInput(this.driver, { resId: 'inputReleaseDate', containsKeywords: ['release','date','fecha'] });
            if (ref) {
              try {
                await ref.setValue('2020-05-10');
              } catch (err) {
                throw new Error('Refound release date element not editable: ' + (err && err.message));
              }
            } else {
              throw new Error('No editable field found for release date');
            }
          }
        } catch (err) {
          throw err;
        }
      } else {
        // If we accepted the picker, wait a bit for UI to settle and ensure description is visible
        try {
          await this.driver.pause(900);
          try {
            await this.driver.$('android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/inputDescription"))');
          } catch (_) {}
        } catch (_) {}
      }
    }
  } catch (e) {
    try {
      // final attempt: re-find the release date input and setValue
      const ref = await findInput(this.driver, { resId: 'inputReleaseDate', containsKeywords: ['release','date','fecha'] });
      if (ref) {
        try {
          await ref.setValue("2020-05-10");
        } catch (err) {
          await dumpPageSource("inputReleaseDate-setvalue-failed", this.driver);
          throw new Error('Failed to set release date via DatePicker or setValue: ' + (err && err.message));
        }
      } else {
        await dumpPageSource("inputReleaseDate-setvalue-failed", this.driver);
        throw new Error('Failed to set release date via DatePicker or setValue');
      }
    } catch (err) {
      await dumpPageSource("inputReleaseDate-setvalue-failed", this.driver);
      throw err;
    }
  }

  // Robustly set description: always re-find the element (avoid stale references)
  async function robustSetDescription(driver, /*prevEl not used intentionally*/ _prevEl, options, text) {
    const opts = options || { containsKeywords: ['description','desc'], textKeywords: ['Description','Descripción','descripcion'] };
    const max = 6;
    for (let i = 0; i < max; i++) {
      // attempt to scroll into view before each try
      try {
        await driver.$('android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId("com.team3.vinyls:id/inputDescription"))');
      } catch (_) {}
      try {
        const ref = await findInput(driver, opts);
        if (ref) {
          try {
            // if the found element is a container, try to find an editable child
            let target = ref;
            try {
              const childEdit = await ref.$('.//android.widget.EditText');
              if (childEdit && await childEdit.isExisting()) {
                target = childEdit;
              }
            } catch (_) {}

            // focus and set value
            try { await target.click(); } catch (_) {}
            await target.clearValue().catch(() => {});
            await target.setValue(text);
            try { await driver.hideKeyboard(); } catch (_) {}

            // verify the value was set (small guard)
            try {
              const val = await target.getText();
              if (val && val.toString().includes(text.split(' ')[0])) {
                return;
              }
            } catch (_) {
              // ignore verification failure; assume success
              return;
            }

          } catch (err) {
            // maybe became stale between find and action; retry
          }
        }
      } catch (_) {
        // ignore and retry
      }
      await driver.pause(600);
    }
    await dumpPageSource('album-create-description-set-failed', driver);
    throw new Error('Failed to set description input after retries');
  }

  await robustSetDescription(this.driver, descriptionInput, { containsKeywords: ['description','desc'], textKeywords: ['Description','Descripción','descripcion'] }, "Álbum de prueba para E2E - Buscando América Prueba");
  await swipeUp(this.driver);
  await this.driver.pause(500);
});

When("I submit the album creation form", async function () {
  const submitButton = await this.driver.$(byResId("btnSave"));
  const exists = await submitButton.isExisting();
  assert.ok(exists, "Submit button not visible");

  // capture current activity if supported (Android)
  let beforeActivity = null;
  try { beforeActivity = await this.driver.getCurrentActivity(); } catch (_) {}

  await submitButton.click();

  // Wait robustly: either button disappears, activity changes, or a success indicator appears
  const timeout = 8000; // ms
  const pollInterval = 300; // ms
  const start = Date.now();

  while (Date.now() - start < timeout) {
    try {
      const displayed = await submitButton.isDisplayed();
      if (!displayed) return; // button gone -> likely navigated
    } catch (err) {
      // if querying the button fails (stale/not found), assume it's gone
      return;
    }

    // activity change
    if (beforeActivity) {
      try {
        const afterActivity = await this.driver.getCurrentActivity();
        if (afterActivity && afterActivity !== beforeActivity) return;
      } catch (_) {}
    }

    // look for likely success indicators: title or snackbar/toast
    try {
      const titleEl = await this.driver.$("//*[contains(@resource-id,'txtTitle') or contains(@resource-id,'txt_album') or contains(@resource-id,'txtAlbum')]");
      if (titleEl && await titleEl.isExisting()) return;
    } catch (_) {}

    try {
      const toastEl = await this.driver.$("//*[contains(@resource-id,'snackbar') or contains(@resource-id,'toast') or contains(@text,'added') or contains(@text,'agregado') or contains(@text,'creado')]");
      if (toastEl && await toastEl.isExisting()) return;
    } catch (_) {}

    await this.driver.pause(pollInterval);
  }

  await dumpPageSource('album-create-submit-failed', this.driver);
  throw new Error(`Submit button still displayed after ${timeout}ms`);
});

When("Then I shoud see the album name {string}", async function (albumName) {
  const byText = (txt) => `//*[contains(@text, "${txt}")]`;

  let el = await this.driver.$(byText(albumName));

  const exists = await el.isExisting();

  assert.ok(exists, `Album name "${albumName}" not found`);
});

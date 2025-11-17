const { Given, When, Then } = require("@cucumber/cucumber");
const assert = require("assert");

const byResId = (id) => `//*[@resource-id="com.team3.vinyls:id/${id}"]`;

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

When("I tap the create album button", async function () {
  const fab = await this.driver.$(byResId("fabAddTrack"));

  const exists = await fab.isExisting();
  assert.ok(exists, "Create Album button not visible");
  await fab.click();
  await this.driver.pause(500);
});

When("I fill the album creation form", async function () {
  const titleInput = await this.driver.$(byResId("inputTitle"));
  const descriptionInput = await this.driver.$(byResId("inputDescription"));
  const releaseDateInput = await this.driver.$(byResId("inputReleaseDate"));
  const genreInput = await this.driver.$(byResId("inputGenre"));
  const recordLabelInput = await this.driver.$(byResId("inputLabel"));
  const coverUrlInput = await this.driver.$(byResId("inputCoverUrl"));

  assert.ok(await titleInput.isExisting(), "Title input not found");
  assert.ok(await coverUrlInput.isExisting(), "Cover URL input not found");
  assert.ok(
    await releaseDateInput.isExisting(),
    "Release Date input not found"
  );
  assert.ok(await genreInput.isExisting(), "Genre input not found");
  assert.ok(
    await recordLabelInput.isExisting(),
    "Record Label input not found"
  );
  assert.ok(await descriptionInput.isExisting(), "Description input not found");

  await coverUrlInput.setValue("https://picsum.photos/seed/album1/600/600");
  await titleInput.setValue("Buscando América Prueba");
  await genreInput.setValue("Salsa");
  await recordLabelInput.setValue("EMI");
  await releaseDateInput.setValue("2020-05-10");
  await descriptionInput.setValue(
    "Álbum de prueba para E2E - Buscando América Prueba"
  );
  await swipeUp(this.driver);
  await this.driver.pause(500);
});

When("I submit the album creation form", async function () {
  const submitButton = await this.driver.$(byResId("btnSave"));
  const exists = await submitButton.isExisting();
  assert.ok(exists, "Submit button not visible");
  await submitButton.click();

  await submitButton.waitForDisplayed({ reverse: true, timeout: 8000 });
});

When("Then I shoud see the album name {string}", async function (albumName) {
  const byText = (txt) => `//*[contains(@text, "${txt}")]`;

  let el = await this.driver.$(byText(albumName));

  const exists = await el.isExisting();

  assert.ok(exists, `Album name "${albumName}" not found`);
  
});
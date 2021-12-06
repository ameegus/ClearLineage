function getel(el) {
  return document.getElementById(el);
}

function removeDuplicates(array) {
  return [...new Set(array)];
}

Array.prototype.getSafe = function (index) {
  if (index >= this.length) {
    return null;
  }
  return this[index];
};

/**
 * Shuffles array in place.
 * @param {Array} a items An array containing the items.
 * from https://stackoverflow.com/questions/6274339/how-can-i-shuffle-an-array
 */
function shuffle(a) {
  var j, x, i;
  for (i = a.length - 1; i > 0; i--) {
    j = Math.floor(Math.random() * (i + 1));
    x = a[i];
    a[i] = a[j];
    a[j] = x;
  }
  return a;
}

let motds = [
  "Transparency for LineageOS",
  "Transparency since Q",
  "Fixing Google's design since S",
  "Enhancing design since Q"
];

let motd = null;
function newMOTD() {
  let newMotd = motds[Math.floor(Math.random() * motds.length)];
  if (newMotd === motd) return newMOTD();
  motd = newMotd
  getel("motd").innerText = newMotd;
}
newMOTD();

let initialInnerHTMLs = {};
function renderHandlebars(element, params) {
  if (initialInnerHTMLs[element.id] == null) {
    initialInnerHTMLs[element.id] = element.innerHTML;
  }

  element.classList.remove("hidden");
  element.innerHTML = Handlebars.compile(initialInnerHTMLs[element.id])(params);
}

Handlebars.registerHelper("json", function (context) {
  return JSON.stringify(context);
});

let pictures = [];
let versions = [];
let categories = [];
let devices = [];
let blurs = [];
let themes = [];
let authors = [];
let tableRows = [];
let filtersSelected = [];

fetch("pictures.json")
  .then((response) => response.json())
  .then(buildPage);

function buildPage(devicesjson) {
  pictures = devicesjson;
  for (let picture of pictures) {
    let split = picture.split(".")[0].split("_");
    versions.push(split[0]);
    categories.push(split[1]);
    devices.push(split[2]);
    blurs.push(split[3]);
    themes.push(split[4]);
    authors.push(split[5]);
  }
  versions = removeDuplicates(versions).sort();
  categories = removeDuplicates(categories).sort();
  devices = removeDuplicates(devices).sort();
  blurs = removeDuplicates(blurs).sort().filter(v => v !== "undefined");
  themes = removeDuplicates(themes).sort().filter(v => v !== "undefined");
  authors = removeDuplicates(authors).sort();

  for (let i = 0; i < Math.max(versions.length, categories.length, devices.length); i++) {
    tableRows.push({
      version: versions.getSafe(i),
      category: categories.getSafe(i),
      device: devices.getSafe(i),
      blur: blurs.getSafe(i),
      theme: themes.getSafe(i),
    });
  }

  renderHandlebars(getel("filters"), { tableRows });
  updatePictures();
}

function updateFilters(el, key, value) {
  if (el.checked) {
    filtersSelected.push({
      key,
      value,
    });
  } else {
    filtersSelected = filtersSelected.filter((e) => {
      return e.key != key || e.value != value;
    });
  }
  updatePictures();
}

let pictureSetBefore = null;

function updatePictures() {
  let pictures = getPictures();
  if (pictureSetBefore == null) {
    pictureSetBefore = pictures;
  }
  pictures = shuffle(pictures)

  renderHandlebars(getel("images"), {
    pictures: pictureSetBefore.map((e) => ({
      url: "images/" + e,
      remove: pictures.includes(e) ? "" : "fade-out",
    })),
  });
  setTimeout(() => {
    renderHandlebars(getel("images"), {
      pictures: pictures.map((e) => ({
        url: "images/" + e,
        remove: pictureSetBefore.includes(e) ? "" : "fade-in",
      })),
    });
    pictureSetBefore = pictures;
    renderPictures();
  }, 200);

  renderPictures();
}

function renderPictures() {
  // Code from: https://codepen.io/DarkoKukovec/pen/mgowGG (thanks)
  document.querySelectorAll(".image-grid .item img").forEach((img) => {
    // Ideally, we would know the image size or aspect ratio beforehand...
    if (img.naturalHeight) {
      setItemRatio.call(img);
    } else {
      img.addEventListener("load", setItemRatio);
    }
  });

  function setItemRatio() {
    this.parentNode.style.setProperty("--ratio", this.naturalHeight / this.naturalWidth);
  }
}

function getPictures() {
  if (filtersSelected.length == 0) {
    return pictures;
  }
  let result = [];

  let versionfilter = [];
  let categoryfilter = [];
  let devicefilter = [];
  let blurfilter = [];
  let themefilter = [];
  let authorfilter = [];
  for (let filter of filtersSelected) {
    switch (filter.key) {
      default:
      case "version":
        versionfilter.push(filter.value);
        break;
      case "category":
        categoryfilter.push(filter.value);
        break;
      case "device":
        devicefilter.push(filter.value);
        break;
      case "blur":
        blurfilter.push(filter.value);
        break;
      case "theme":
        themefilter.push(filter.value);
        break;
      case "author":
        authorfilter.push(filter.value);
        break;
    }
  }
  if (versionfilter.length == 0) versionfilter = versions;
  if (categoryfilter.length == 0) categoryfilter = categories;
  if (devicefilter.length == 0) devicefilter = devices;
  if (blurfilter.length == 0) blurfilter = blurs;
  if (themefilter.length == 0) themefilter = themes;
  if (authorfilter.length == 0) authorfilter = authors;

  if (blurfilter.includes("noblur"))
    blurfilter.push("undefined");
  themefilter.push("undefined");

  for (let picture of pictures) {
    let imageAttributes = picture.split(".")[0].split("_")
    if (versionfilter.includes(imageAttributes[0])
      && categoryfilter.includes(imageAttributes[1])
      && devicefilter.includes(imageAttributes[2])
      && blurfilter.includes(imageAttributes[3])
      && themefilter.includes(imageAttributes[4])
      && authorfilter.includes(imageAttributes[5])) {
      result.push(picture);
    }
  }

  return removeDuplicates(result);
}

function createTableElement(text) {
  let el = document.createElement("td");
  let input = document.createElement("input");
  input.type = "checkbox";
  input.value = text;
  input.id = "checkbox-" + text;
  input.checked = true;
  el.appendChild(input);
  let label = document.createElement("label");
  label.setAttribute("for", "checkbox-" + text);
  label.innerText = text;
  el.appendChild(label);
  return el;
}

//https://stackoverflow.com/a/64807286 adapted for empty cells
const transposeTable = (tbody, newContainerType = "tbody") => {
  const rows = Array.from(tbody.querySelectorAll("tr"));
  const newTbody = document.createElement(newContainerType);

  for (let rowIdx = 0; rowIdx < rows.length; rowIdx++) {
    const row = rows[rowIdx];
    const cells = Array.from(row.querySelectorAll("td, th"));

    for (let cellIdx = 0; cellIdx < cells.length; cellIdx++) {
      const cell = cells[cellIdx];
      const newRow = newTbody.children[cellIdx] || document.createElement("tr");
      if (!newTbody.children[cellIdx]) {
        newTbody.appendChild(newRow);
      }
      while (newRow.children.length < rowIdx) newRow.appendChild(document.createElement("td"));
      newRow.appendChild(cell.cloneNode(true));
    }
  }
  tbody.parentElement.appendChild(newTbody);
  tbody.parentElement.removeChild(tbody);
};

let displayedImage = null;

function displayImage(img) {
  let el = getel("display-image");
  if (img == null) {
    el.classList.remove("visible");
    displayedImage = null;
  } else {
    el.classList.add("visible");
    displayedImage = img.split("/")[1];
    let imgData = img.split("/")[1];
    let imgText = getImageDescription(imgData);
    renderHandlebars(el, { img, imgText });
  }
}

function getImageDescription(img) {
  let imgData = img.split(".")[0].split("_");
  let text = imgData[1] + " on " + imgData[2] + " @ LineageOS " + imgData[0]
    + (imgData[4] !== "undefined" ? " (" + imgData[4] + " theme)" : "")
    + " | Screenshot by " + imgData[5];
  return text;
}

function darkTheme(value) {
  document.cookie = "darkTheme=" + value;
  if (value) {
    document.querySelector("body").classList.add("dark");
  } else {
    document.querySelector("body").classList.remove("dark");
  }
}
if (document.cookie == "darkTheme=false") {
  darkTheme(false);
}

function changeDisplayedImage(diff) {
  if (displayedImage == null) return;
  if (pictureSetBefore.length <= 0) return;
  let index = pictureSetBefore.indexOf(displayedImage);
  if (index < 0 || index >= pictures.length) return;
  let newIndex = (pictureSetBefore.length + index + diff) % pictureSetBefore.length;
  displayImage("images/" + pictureSetBefore[newIndex]);
}

document.onkeydown = function (event) {
  switch (event.keyCode) {
    case 27:
      // escape
      displayImage(null);
    case 37:
      // left
      changeDisplayedImage(-1);
      break;
    case 39:
      // right
      changeDisplayedImage(1);
      break;
  }
};

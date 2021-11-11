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

let motds = [
  "Transparency for LineageOS",
  "Transparency since R",
  "Fixing Google's design since S",
  "Enhancing design since R"
];

function newMOTD() {
  getel("motd").innerText = motds[Math.floor(Math.random() * motds.length)];
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
  }
  versions = removeDuplicates(versions);
  categories = removeDuplicates(categories);
  devices = removeDuplicates(devices);

  for (let i = 0; i < Math.max(versions.length, categories.length, devices.length); i++) {
    tableRows.push({
      version: versions.getSafe(i),
      category: categories.getSafe(i),
      device: devices.getSafe(i),
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
    }
  }
  if (versionfilter.length == 0) versionfilter = versions;
  if (categoryfilter.length == 0) categoryfilter = categories;
  if (devicefilter.length == 0) devicefilter = devices;

  for (let picture of pictures) {
    let imageAttributes = picture.split(".")[0].split("_")
    if (versionfilter.includes(imageAttributes[0])
      && categoryfilter.includes(imageAttributes[1])
      && devicefilter.includes(imageAttributes[2])) {
      result.push(picture);
    }
  }

  return removeDuplicates(result);
}

function buildTable() {
  for (let version of versions) getel("versions").appendChild(createTableElement(version));
  for (let category of categories) getel("categories").appendChild(createTableElement(category));
  for (let device of devices) getel("devices").appendChild(createTableElement(device));
  transposeTable(document.querySelector("#form table tbody"));
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

function displayImage(img) {
  let el = getel("display-image");
  if (img === null) {
    el.classList.remove("visible");
  } else {
    el.classList.add("visible");
    let imgData = img.split("/")[1].split(".")[0].split("_");
    let imgText = imgData[1] + " on " + imgData[2] + " @ LineageOS " + imgData[0];
    renderHandlebars(el, { img, imgText });
  }
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

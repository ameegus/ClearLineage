function getel(el) {
  return document.getElementById(el)
}

function removeDuplicates(array) {
  return [...(new Set(array))]
}

var pictures = []
var versions = []
var categories = []
var devices = []

fetch("pictures.json")
  .then(response => response.json())
  .then(buildPage)

function buildPage(devicesjson) {
  pictures = devicesjson
  for (let picture of pictures) {
    let split = picture.split(".")[0].split("_")
    versions.push(split[0])
    categories.push(split[1])
    devices.push(split[2])
  }
  versions = removeDuplicates(versions)
  categories = removeDuplicates(categories)
  devices = removeDuplicates(devices)
  buildTable()
}

function buildTable() {
  for (let version of versions)
    getel("versions").appendChild(createTableElement(version))
  for (let category of categories)
    getel("categories").appendChild(createTableElement(category))
  for (let device of devices)
    getel("devices").appendChild(createTableElement(device))
  transposeTable(document.querySelector("#form table tbody"))
}

function createTableElement(text) {
  let el = document.createElement("td")
  let input = document.createElement("input")
  input.type = "checkbox"
  input.value = text
  input.id = "checkbox-" + text
  input.checked = true
  el.appendChild(input)
  let label = document.createElement("label")
  label.setAttribute("for", "checkbox-" + text)
  label.innerText = text
  el.appendChild(label)
  return el
}

//https://stackoverflow.com/a/64807286 adapted for empty cells
const transposeTable = (tbody, newContainerType = "tbody") => {
  const rows = Array.from(tbody.querySelectorAll("tr"))
  const newTbody = document.createElement(newContainerType)

  for (let rowIdx = 0; rowIdx < rows.length; rowIdx++) {
    const row = rows[rowIdx]
    const cells = Array.from(row.querySelectorAll("td, th"))

    for (let cellIdx = 0; cellIdx < cells.length; cellIdx++) {
      const cell = cells[cellIdx]
      const newRow = newTbody.children[cellIdx] || document.createElement("tr")
      if (!newTbody.children[cellIdx]) {
        newTbody.appendChild(newRow)
      }
      while (newRow.children.length < rowIdx) newRow.appendChild(document.createElement("td"))
      newRow.appendChild(cell.cloneNode(true))
    }
  }
  tbody.parentElement.appendChild(newTbody)
  tbody.parentElement.removeChild(tbody)
}

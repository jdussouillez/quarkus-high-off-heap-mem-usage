#!/usr/bin/env zx

// Disable command logging
$.verbose = false;

const CDN_URL = "https://cdn.quarkus-high-off-heap-mem-usage.jdussouillez.github.com";
const OUTPUT_FILE = "db/server-data.sql";
const N = 1000000;

function randomDouble(min, max) {
    return Math.random() * (max - min) + min;
}

function randomInt(min, max) {
    return Math.floor(randomDouble(min, max));
}

function randomBoolean() {
    return randomInt(0, 2) === 1;
}

function generateValues(n) {
    const id = n.toString().padStart(10, '0');
    return {
        id,
        designation: `Product #${id}`,
        stock: randomInt(0, 1000),
        picture_url: `${CDN_URL}/img/picture/${id}.jpg`,
        blueprint_url: randomBoolean() ? `${CDN_URL}/img/bleuprint/${id}.jpg` : null,
        weight: randomDouble(1, 1000),
        volume: randomDouble(1, 5000),
        obsolete: randomBoolean()
    };
}

function generateInsertQuery(v) {
    const blueprintValue = v.blueprint_url ? `'${v.blueprint_url}'` : "NULL";
    return `INSERT INTO product.products (id, designation, stock, picture_url, blueprint_url, weight, volume, obsolete) VALUES('${v.id}', '${v.designation}', ${v.stock}, '${v.picture_url}', ${blueprintValue}, ${v.weight.toPrecision(4)}, ${v.volume.toPrecision(4)}, ${v.obsolete});`;
}

function write(value, append) {
    const options = {
        "encoding": "utf8",
    };
    if (append) {
        options.flag = "a+";
    }
    fs.writeFileSync(OUTPUT_FILE, value, options);
}

function generateDataFile() {
    write("TRUNCATE product.products;");
    Array.from({length: N}, (_, n) => generateValues(n + 1))
        .map(v => generateInsertQuery(v))
        .forEach(sql => write(sql + "\n", true));
}

generateDataFile();

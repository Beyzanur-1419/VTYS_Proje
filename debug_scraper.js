const puppeteer = require('puppeteer');
const path = require('path');

(async () => {
  const browser = await puppeteer.launch({ headless: "new" });
  const page = await browser.newPage();
  const filePath = path.join(__dirname, 'uploads/debug_trendyol.html');
  const url = `file://${filePath}`;
  
  console.log(`Loading ${url}...`);
  await page.goto(url, { waitUntil: 'domcontentloaded' });

  const data = await page.evaluate(() => {
    // 1. Inspect Window Props
    const props = {};
    Object.keys(window).filter(k => k.startsWith('__')).forEach(k => {
        try {
            const str = JSON.stringify(window[k]);
            props[k] = str.length > 200 ? str.substring(0, 200) + "..." : str;
        } catch (e) {
            props[k] = "Error stringifying";
        }
    });

    // 2. Find Price Element Hierarchy
    function containsPrice(text) {
        return text && (text.includes('TL') || text.includes('₺') || text.includes('tl'));
    }

    const allElements = Array.from(document.querySelectorAll('*'));
    const priceElements = allElements.filter(el => 
      containsPrice(el.textContent) && el.children.length === 0
    );

    let hierarchy = [];
    if (priceElements.length > 0) {
        let current = priceElements[0];
        // Go up 10 levels
        for (let i = 0; i < 10; i++) {
            if (!current || current.tagName === 'BODY') break;
            hierarchy.push({
                tag: current.tagName,
                class: current.className,
                id: current.id
            });
            current = current.parentElement;
        }
    }

    return {
        windowProps: props,
        priceHierarchy: hierarchy
    };
  });

  console.log(JSON.stringify(data, null, 2));
  await browser.close();
})();

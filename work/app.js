// ── Mock product data ──
const products = [
  { id: 1,  emoji: "💻", title: "Laptop Pro 15\" — Intel i7, 16GB RAM, 512GB SSD", price: 899.99, original: 1199.99, rating: 4, reviews: 2341, badge: "Best Seller", prime: true, category: "Electronics" },
  { id: 2,  emoji: "📱", title: "Smartphone X12 — 6.7\" AMOLED, 128GB, 5G Ready", price: 499.00, original: 649.00, rating: 4, reviews: 5820, badge: "Deal",        prime: true, category: "Electronics" },
  { id: 3,  emoji: "🎧", title: "Wireless Noise-Cancelling Headphones — 30hr Battery", price: 79.99,  original: 129.99, rating: 5, reviews: 9102, badge: null,         prime: true, category: "Electronics" },
  { id: 4,  emoji: "👟", title: "Running Shoes — Lightweight Mesh, Men's Size 8–13", price: 54.95,  original: 89.95,  rating: 4, reviews: 1234, badge: null,         prime: true, category: "Clothing"    },
  { id: 5,  emoji: "📚", title: "Clean Code: A Handbook of Agile Software Craftsmanship", price: 29.99,  original: 39.99,  rating: 5, reviews: 7654, badge: "Best Seller", prime: false, category: "Books"       },
  { id: 6,  emoji: "🪑", title: "Ergonomic Office Chair — Lumbar Support, Adjustable Arms", price: 249.00, original: 349.00, rating: 4, reviews: 876,  badge: "Deal",        prime: true, category: "Home & Garden"},
  { id: 7,  emoji: "⌨️", title: "Mechanical Keyboard — RGB Backlit, TKL Layout, Blue Switches", price: 69.99,  original: 99.99,  rating: 4, reviews: 3210, badge: null,         prime: true, category: "Electronics" },
  { id: 8,  emoji: "🎮", title: "Gaming Controller — Wireless, Compatible with PC & Console", price: 44.99,  original: 59.99,  rating: 4, reviews: 4500, badge: "Deal",        prime: true, category: "Electronics" },
  { id: 9,  emoji: "🧴", title: "Vitamin C Serum — 20% Concentration, 1 fl oz", price: 18.99,  original: 24.99,  rating: 4, reviews: 12300, badge: "Best Seller", prime: true, category: "Health"       },
  { id: 10, emoji: "🏋️", title: "Adjustable Dumbbell Set — 5–52.5 lbs, Space Saving", price: 189.00, original: 299.00, rating: 5, reviews: 2100, badge: null,         prime: true, category: "Sports"      },
  { id: 11, emoji: "📷", title: "Mirrorless Camera — 24MP, 4K Video, Kit Lens Included", price: 749.00, original: 999.00, rating: 4, reviews: 654,  badge: null,         prime: false, category: "Electronics" },
  { id: 12, emoji: "🕯️", title: "Scented Candle Set — 6 Pack, Soy Wax, 40hr Burn Time", price: 24.99,  original: 34.99,  rating: 4, reviews: 3300, badge: "Best Seller", prime: true, category: "Home & Garden"},
];

let cartCount = 0;

// ── Render products ──
function renderProducts(list) {
  const grid = document.getElementById("productGrid");
  const countEl = document.getElementById("resultsCount");

  countEl.textContent = `Showing ${list.length} result${list.length !== 1 ? "s" : ""}`;

  if (list.length === 0) {
    grid.innerHTML = `<div class="no-results">😕 No products found. Try a different search.</div>`;
    return;
  }

  grid.innerHTML = list.map(p => {
    const discount = Math.round((1 - p.price / p.original) * 100);
    const stars = "★".repeat(p.rating) + "☆".repeat(5 - p.rating);
    return `
      <div class="product-card" onclick="selectProduct(${p.id})">
        <div class="product-img">${p.emoji}</div>
        ${p.badge ? `<span class="product-badge">${p.badge}</span>` : ""}
        <div class="product-title">${p.title}</div>
        <div class="product-rating">${stars}</div>
        <div class="product-review-count">${p.reviews.toLocaleString()} reviews</div>
        <div class="product-price"><span class="currency">$</span>${p.price.toFixed(2)}</div>
        <div class="product-original-price">List: $${p.original.toFixed(2)}</div>
        <div class="product-discount">Save ${discount}%</div>
        ${p.prime ? `<div class="product-prime">✔ Prime — FREE Delivery</div>` : ""}
        <button class="add-to-cart-btn" onclick="addToCart(event, ${p.id})">Add to Cart</button>
      </div>
    `;
  }).join("");
}

// ── Search ──
function handleSearch() {
  const query = document.getElementById("searchInput").value.trim().toLowerCase();
  if (!query) {
    renderProducts(products);
    return;
  }
  const filtered = products.filter(p =>
    p.title.toLowerCase().includes(query) ||
    p.category.toLowerCase().includes(query)
  );
  renderProducts(filtered);
}

// Allow Enter key to search
document.addEventListener("DOMContentLoaded", () => {
  renderProducts(products);

  document.getElementById("searchInput").addEventListener("keydown", e => {
    if (e.key === "Enter") handleSearch();
  });
});

// ── Cart ──
function addToCart(event, id) {
  event.stopPropagation(); // don't trigger card click
  cartCount++;
  document.querySelector(".cart-count").textContent = cartCount;
  const btn = event.target;
  btn.textContent = "Added ✓";
  btn.style.background = "#c8f7c5";
  setTimeout(() => {
    btn.textContent = "Add to Cart";
    btn.style.background = "";
  }, 1200);
}

// ── Product select (placeholder for detail page) ──
function selectProduct(id) {
  const p = products.find(x => x.id === id);
  if (p) alert(`Product: ${p.title}\nPrice: $${p.price.toFixed(2)}\n\n(Detail page coming in MVP2)`);
}

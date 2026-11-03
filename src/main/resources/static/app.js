const API = "/api/books";
const $ = (id) => document.getElementById(id);
let books = [];
let editingId = null;
let purchasingBook = null;
let deletingId = null;
let toastTimer;

const money = (value) => new Intl.NumberFormat("en-IN", {
  style: "currency", currency: "INR", maximumFractionDigits: 2
}).format(Number(value || 0));

async function api(path = "", options = {}) {
  const response = await fetch(`${API}${path}`, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
  });
  if (response.status === 204) return null;
  const data = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(data.error || `Request failed (${response.status})`);
  return data;
}

function notify(message) {
  const toast = $("toast");
  toast.textContent = message;
  toast.classList.add("show");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove("show"), 2800);
}

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, char => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  })[char]);
}

async function loadBooks() {
  try {
    books = await api();
    render();
  } catch (error) {
    $("bookRows").innerHTML = `<tr><td colspan="5" class="empty-state">${escapeHtml(error.message)}. Make sure the Java application is running.</td></tr>`;
    notify(error.message);
  }
}

function render() {
  const query = $("searchInput").value.trim().toLowerCase();
  const sort = $("sortSelect").value;
  let visible = books.filter(book =>
    [book.id, book.title, book.author].some(value => value.toLowerCase().includes(query))
  );
  visible.sort((a, b) => sort === "price" ? Number(a.price) - Number(b.price)
    : sort === "stock" ? a.quantity - b.quantity
    : a.title.localeCompare(b.title));

  $("totalTitles").textContent = books.length;
  $("totalCopies").textContent = books.reduce((sum, book) => sum + book.quantity, 0);
  $("inventoryValue").textContent = money(books.reduce((sum, book) => sum + Number(book.price) * book.quantity, 0));
  $("lowStock").textContent = books.filter(book => book.quantity <= 3).length;
  $("resultCount").textContent = `${visible.length} ${visible.length === 1 ? "book" : "books"}`;
  $("footerCount").textContent = `Showing ${visible.length} of ${books.length} titles`;

  if (!visible.length) {
    $("bookRows").innerHTML = `<tr><td colspan="5" class="empty-state">${query ? "No books match your search." : "Your collection is empty. Add your first book to get started."}</td></tr>`;
    return;
  }
  $("bookRows").innerHTML = visible.map(book => {
    const stockClass = book.quantity === 0 ? "out" : book.quantity <= 3 ? "low" : "good";
    const stockLabel = book.quantity === 0 ? "Out of stock" : book.quantity <= 3 ? `Only ${book.quantity} left` : `${book.quantity} available`;
    const initials = book.title.trim().charAt(0).toUpperCase();
    return `<tr>
      <td><div class="book-cell"><div class="mini-cover">${escapeHtml(initials)}</div><div><div class="book-name">${escapeHtml(book.title)}</div><div class="book-author">by ${escapeHtml(book.author)}</div></div></div></td>
      <td><span class="id-chip">${escapeHtml(book.id)}</span></td>
      <td><span class="price">${money(book.price)}</span></td>
      <td><span class="stock ${stockClass}">${stockLabel}</span></td>
      <td><div class="row-actions">
        <button class="small-btn buy" data-action="buy" data-id="${escapeHtml(book.id)}" ${book.quantity === 0 ? "disabled" : ""}>Sell</button>
        <button class="small-btn" data-action="edit" data-id="${escapeHtml(book.id)}">Edit</button>
        <button class="small-btn" data-action="delete" data-id="${escapeHtml(book.id)}">Delete</button>
      </div></td></tr>`;
  }).join("");
}

function openAddDialog() {
  editingId = null;
  $("bookForm").reset();
  $("bookId").disabled = false;
  $("formEyebrow").textContent = "NEW ARRIVAL";
  $("formTitle").textContent = "Add a book";
  $("saveBookBtn").textContent = "Save book";
  $("bookError").textContent = "";
  $("bookDialog").showModal();
}

function openEditDialog(book) {
  editingId = book.id;
  $("bookForm").reset();
  $("bookId").value = book.id;
  $("bookId").disabled = true;
  $("bookTitle").value = book.title;
  $("bookAuthor").value = book.author;
  $("bookPrice").value = book.price;
  $("bookQuantity").value = book.quantity;
  $("formEyebrow").textContent = "UPDATE DETAILS";
  $("formTitle").textContent = "Edit book";
  $("saveBookBtn").textContent = "Save changes";
  $("bookError").textContent = "";
  $("bookDialog").showModal();
}

$("bookForm").addEventListener("submit", async event => {
  event.preventDefault();
  const payload = {
    id: $("bookId").value.trim(),
    title: $("bookTitle").value.trim(),
    author: $("bookAuthor").value.trim(),
    price: Number($("bookPrice").value),
    quantity: Number($("bookQuantity").value)
  };
  try {
    if (editingId) await api(`/${encodeURIComponent(editingId)}`, { method: "PUT", body: JSON.stringify(payload) });
    else await api("", { method: "POST", body: JSON.stringify(payload) });
    $("bookDialog").close();
    await loadBooks();
    notify(editingId ? "Book details updated." : "Book added to your collection.");
  } catch (error) {
    $("bookError").textContent = error.message;
  }
});

function openPurchase(book) {
  purchasingBook = book;
  $("purchaseTitle").textContent = book.title;
  $("purchaseMeta").textContent = `${book.id} · ${money(book.price)} each · ${book.quantity} available`;
  $("purchaseQty").value = 1;
  $("purchaseQty").max = book.quantity;
  $("purchaseError").textContent = "";
  updatePurchaseTotal();
  $("purchaseDialog").showModal();
}
function updatePurchaseTotal() {
  if (!purchasingBook) return;
  const qty = Math.max(1, Number($("purchaseQty").value) || 1);
  $("purchaseTotal").textContent = money(qty * Number(purchasingBook.price));
  $("confirmPurchase").disabled = qty > purchasingBook.quantity || qty < 1;
  $("purchaseError").textContent = qty > purchasingBook.quantity ? `Only ${purchasingBook.quantity} copies are available.` : "";
}
$("purchaseQty").addEventListener("input", updatePurchaseTotal);
$("qtyMinus").addEventListener("click", () => { $("purchaseQty").value = Math.max(1, Number($("purchaseQty").value) - 1); updatePurchaseTotal(); });
$("qtyPlus").addEventListener("click", () => { $("purchaseQty").value = Math.min(purchasingBook?.quantity || 1, Number($("purchaseQty").value) + 1); updatePurchaseTotal(); });
$("purchaseForm").addEventListener("submit", async event => {
  event.preventDefault();
  if (!purchasingBook) return;
  try {
    const result = await api(`/${encodeURIComponent(purchasingBook.id)}/purchase`, {
      method: "POST", body: JSON.stringify({ quantity: Number($("purchaseQty").value) })
    });
    $("purchaseDialog").close();
    await loadBooks();
    notify(`Purchase complete · ${money(result.total)} · ${result.remainingStock} left`);
  } catch (error) {
    $("purchaseError").textContent = error.message;
  }
});

function askDelete(book) {
  deletingId = book.id;
  $("deleteMessage").textContent = `“${book.title}” will be permanently removed from your inventory.`;
  $("confirmDialog").showModal();
}
$("confirmForm").addEventListener("submit", async event => {
  event.preventDefault();
  try {
    await api(`/${encodeURIComponent(deletingId)}`, { method: "DELETE" });
    $("confirmDialog").close();
    await loadBooks();
    notify("Book deleted.");
  } catch (error) { notify(error.message); }
});

$("bookRows").addEventListener("click", event => {
  const button = event.target.closest("button[data-action]");
  if (!button) return;
  const book = books.find(item => item.id === button.dataset.id);
  if (!book) return;
  if (button.dataset.action === "edit") openEditDialog(book);
  if (button.dataset.action === "buy") openPurchase(book);
  if (button.dataset.action === "delete") askDelete(book);
});
$("addBookBtn").addEventListener("click", openAddDialog);
$("inventoryNav").addEventListener("click", () => $("inventory").scrollIntoView({ behavior: "smooth" }));
$("searchInput").addEventListener("input", render);
$("sortSelect").addEventListener("change", render);
document.querySelectorAll("[data-close]").forEach(button => {
  button.addEventListener("click", () => $(button.dataset.close).close());
});
$("today").textContent = new Intl.DateTimeFormat("en-IN", { dateStyle: "medium" }).format(new Date());
loadBooks();

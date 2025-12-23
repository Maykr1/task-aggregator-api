document.addEventListener("DOMContentLoaded", () => {
    const toastEl = document.getElementById("appToast");
    
    const message = toastEl.dataset.message;
    if (!message || message.trim() === "") return;

    // Inject
    toastEl.querySelector(".toast-body").textContent = message;

    // Show toast
    const toast = new bootstrap.Toast(toastEl, {
        delay: 5000
    });

    toast.show();
});
document.addEventListener("DOMContentLoaded", function () {
  console.log("✅ DOM cargado.");
  var menuIcon = document.getElementById("menuIcon");

  if (menuIcon) {
    console.log("✅ Menú encontrado.");
    menuIcon.onclick = function () {
      alert("¡Menú clickeado!");
    };
  } else {
    console.log("❌ No se encontró el menú.");
  }
});

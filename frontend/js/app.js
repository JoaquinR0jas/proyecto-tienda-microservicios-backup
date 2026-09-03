// al apretar comprar aviso no mas, no tengo carrito armado todavia
document.addEventListener("DOMContentLoaded", function () {
    var botones = document.querySelectorAll(".comprar");
    botones.forEach(function (boton) {
        boton.addEventListener("click", function () {
            alert("Agregado al carrito (por ahora es solo una alerta)");
        });
    });

    var form = document.getElementById("formContacto");
    form.addEventListener("submit", function (e) {
        e.preventDefault();
        alert("Mensaje enviado, te contactamos pronto");
        form.reset();
    });
});

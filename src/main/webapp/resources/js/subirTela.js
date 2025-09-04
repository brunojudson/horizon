
// Função para rolar até a primeira mensagem dinâmica visível e com conteúdo
function scrollToFirstMessage() {
    // Seleciona todos os containers de mensagens relevantes
    var msgs = document.querySelectorAll('.ui-messages, .ui-messages-error, .ui-messages-warn, .ui-messages-info, .ui-messages-success');
    for (var i = 0; i < msgs.length; i++) {
        var el = msgs[i];
        // Ignora elementos não visíveis
        if (el.offsetParent === null) continue;
        // Ignora painéis estáticos sem mensagens dinâmicas
        var hasMsg = false;
        // PrimeFaces normalmente coloca mensagens em li ou span
        var items = el.querySelectorAll('li, span, .ui-message-error, .ui-message-info, .ui-message-warn, .ui-message-success');
        for (var j = 0; j < items.length; j++) {
            if (items[j].innerText && items[j].innerText.trim().length > 0) {
                hasMsg = true;
                break;
            }
        }
        if (hasMsg) {
            el.scrollIntoView({ behavior: 'smooth', block: 'center' });
            break;
        }
    }
}

// Após carregamento inicial
document.addEventListener('DOMContentLoaded', function() {
    scrollToFirstMessage();
});

// Após qualquer Ajax do PrimeFaces
if (window.PrimeFaces && window.addEventListener) {
    window.addEventListener('pfAjaxComplete', function() {
        setTimeout(function() {
            scrollToFirstMessage();
        }, 50);
    });
}
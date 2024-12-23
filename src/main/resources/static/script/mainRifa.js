
/*************************************
 * Função: toggleMenu
 * Descrição: Alterna a exibição do menu de navegação em dispositivos móveis.
 *************************************/
document.querySelector('.navbar-toggler').addEventListener('click', function () {
    document.querySelector('.navbar-links').classList.toggle('show');
});

/*************************************
 * Função: gerarNumero
 * Descrição: Faz uma requisição POST ao backend para gerar um número da rifa.
 * Parâmetros:
 *   - id: Identificador da rifa.
 *   - numero: Número selecionado.
 *************************************/
function gerarNumero(id, numero) {
    fetch(`/rifa/${id}/comprar/${numero}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ comprador: 'Nome do Comprador' }) // Substitua por valor dinâmico
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro na requisição: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        document.getElementById('numeroGerado').innerText = `Número gerado: ${data.numero}`;
        if (data.qrcode) {
            document.getElementById('qrCodePix').src = data.qrcode;
            document.getElementById('secaoPix').style.display = 'block';
        }
    })
    .catch(err => {
        console.error('Erro ao gerar número:', err);
        alert('Erro ao gerar número. Tente novamente mais tarde.');
    });
}

/*************************************
 * Função: abrirModal
 * Descrição: Exibe o modal e configura o número selecionado.
 * Parâmetros:
 *   - numero: Número da rifa selecionado.
 *************************************/
function abrirModal(numero) {
    const modal = document.getElementById("modalConfirmarCompra"); // Seleciona o modal
    const numeroSelecionadoSpan = document.getElementById("numeroSelecionado"); // Exibe o número no modal
    numeroSelecionadoSpan.textContent = numero; // Atualiza o número selecionado no modal
    modal.style.display = "flex"; // Exibe o modal
}

/*************************************
 * Função: confirmarCompra
 * Descrição: Envia os dados do comprador e número da rifa para o backend.
 * Parâmetros:
 *   - id: ID da rifa.
 *   - numero: Número da rifa selecionado.
 *************************************/
async function confirmarCompra(id, numero) {
    const nome = document.getElementById("nome").value; // Captura o nome do comprador
    const telefone = document.getElementById("telefone").value; // Captura o telefone do comprador

    if (!nome || !telefone) {
        alert("Por favor, preencha todos os campos."); // Validação simples
        return;
    }

    const loading = document.getElementById("loading"); // Exibe o indicador de loading
    loading.style.display = "block";

    try {
        // Envia a requisição para o backend
        const response = await fetch(`/api/rifa/${id}/comprar/${numero}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ comprador: nome, telefone: telefone })
        });

        if (!response.ok) {
            throw new Error("Erro ao processar a compra."); // Verifica a resposta
        }

        const data = await response.json(); // Converte a resposta para JSON

        // Atualiza os detalhes do modal com os dados recebidos
        const detalhesDiv = document.getElementById("detalhesCompra");
        detalhesDiv.innerHTML = `
            <h4>Pagamento PIX</h4>
            <p>QR Code:</p>
            <img src="${data.qrcode}" alt="QR Code PIX">
            <p>Código PIX Copia e Cola:</p>
            <p>${data.pixCopiaCola}</p>
        `;

        const checkVisual = document.getElementById("checkVisual"); // Exibe o check visual
        checkVisual.style.display = "block";

        loading.style.display = "none"; // Esconde o indicador de loading
    } catch (error) {
        console.error("Erro ao confirmar compra:", error);
        alert("Erro ao processar a compra. Tente novamente.");
        loading.style.display = "none"; // Esconde o loading em caso de erro
    }
}

/*************************************
 * Função: fecharModal
 * Descrição: Fecha o modal de confirmação de compra.
 *************************************/
function fecharModal() {
    const modal = document.getElementById("modalConfirmarCompra");
    modal.style.display = "none"; // Esconde o modal
}

/*************************************
 * Função: selecionarNumero
 * Descrição: Adiciona um formulário dinâmico para o usuário preencher seus dados.
 * Parâmetros:
 *   - button: Elemento HTML do botão selecionado.
 *************************************/
function selecionarNumero(button) {
    const numero = button.getAttribute("data-numero");

    const formulario = `
        <div class="container-formulario">
            <h4>Digite seus Dados</h4>
            <form id="formCompra">
                <input type="hidden" id="numeroEscolhido" value="${numero}">
                <label for="nome">Nome:</label>
                <input type="text" id="nome" required>
                <label for="telefone">Telefone (Whatsapp):</label>
                <input type="text" id="telefone" required>
                <button type="button" onclick="confirmarCompra()">Confirmar</button>
            </form>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', formulario);
}

/*************************************
 * Função: Configurar cores dinâmicas
 * Descrição: Aplica cores dinâmicas às caixas da rifa e ajusta os textos e botões.
 *************************************/
document.addEventListener("DOMContentLoaded", function () {
    const container = document.querySelector(".box-container");
    const boxes = Array.from(document.querySelectorAll(".box-rifa"));

    // Cores para os boxes
    const predefinedColors = [
        "#F9E04C", "#39FF5A", "#BEB9B7", "#74C7D5", "#F6C6C6", "#FFCB9A"
    ];

    // Aplica cores iniciais
    boxes.forEach((box, index) => {
        box.style.backgroundColor = predefinedColors[index % predefinedColors.length];
    });

    // Função de Shuffle Fisher-Yates
    function shuffleArray(array) {
        for (let i = array.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [array[i], array[j]] = [array[j], array[i]];
        }
    }

    // Função de animação do Shuffle
    function animateShuffle() {
        // Captura a posição inicial de cada box
        const positions = boxes.map((box) => {
            const rect = box.getBoundingClientRect();
            return { box, x: rect.left, y: rect.top };
        });

        // Shuffle dos elementos
        shuffleArray(boxes);

        // Reordena o DOM (mas não afeta visualmente)
        boxes.forEach((box) => container.appendChild(box));

        // Captura a nova posição e anima todos simultaneamente
        boxes.forEach((box, i) => {
            const oldPos = positions.find(pos => pos.box === box);
            const newRect = box.getBoundingClientRect();

            const dx = oldPos.x - newRect.left;
            const dy = oldPos.y - newRect.top;

            // Define deslocamento inicial para GSAP
            gsap.set(box, { x: dx, y: dy });
            
            // Anima todos simultaneamente
            gsap.to(box, {
                duration: 0.8, // Duração total da animação
                x: 0,
                y: 0,
                ease: "power2.inOut", // Suavização
            });
        });
    }

    // Chama o shuffle a cada 5 segundos
    setInterval(animateShuffle, 5000);
});

/**************************************************************/
/**************************************************************/
// Aguarda o DOM (Document Object Model) ser totalmente carregado
document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("modalConfirmarCompra");
    const closeModal = document.getElementById("closeModal");
    const numeroSelecionadoSpan = document.getElementById("numeroSelecionado");
    const confirmarCompraBtn = document.getElementById("confirmarCompraBtn");
    const loading = document.getElementById("loading");
    const checkVisual = document.getElementById("checkVisual");
    const mensagemSucesso = document.getElementById("mensagemSucesso");

    let numeroSelecionado = null;

    // Função global: Abrir Modal e Capturar o Número Selecionado
    window.abrirModal = function (numero) {
        numeroSelecionado = numero;
        numeroSelecionadoSpan.textContent = numero;
        modal.style.display = "flex";
        checkVisual.style.display = "none"; // Esconde check visual ao abrir
    };

    // Fechar Modal
    closeModal.addEventListener("click", () => {
        modal.style.display = "none";
        loading.style.display = "none"; // Reseta loading
        checkVisual.style.display = "none"; // Reseta check visual
    });

    // Confirmar Compra
    confirmarCompraBtn.addEventListener("click", () => {
        const nome = document.getElementById("nome").value;
        const telefone = document.getElementById("telefone").value;
    
        if (!nome || !telefone) {
            alert("Por favor, preencha todos os campos.");
            return;
        }
    
        // Exibir Loading
        loading.style.display = "block";
        document.getElementById("formCompra").style.display = "none";
    
        fetch(`/api/rifa/1/comprar/${numeroSelecionado}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ comprador: nome, telefone: telefone })
        })
            .then((response) => response.json())
            .then((data) => {
                // Esconder Loading
                loading.style.display = "none";
    
                // Mostrar Check Visual
                checkVisual.style.display = "block";
                mensagemSucesso.textContent = `Rifa de número ${numeroSelecionado} comprada! Verifique seu email.`;
    
                // Fechar Modal após 5 segundos (opcional)
                setTimeout(() => {
                    modal.style.display = "none";
                    checkVisual.style.display = "none";
                    document.getElementById("formCompra").style.display = "block";
                }, 5000);
            })
            .catch((err) => {
                console.error("Erro ao processar a compra:", err);
                alert("Erro ao processar a compra. Tente novamente.");
                loading.style.display = "none";
                document.getElementById("formCompra").style.display = "block";
            });
    });
});

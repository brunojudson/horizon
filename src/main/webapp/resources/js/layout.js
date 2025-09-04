/**

- Layout JavaScript para Sistema BPM
- Controla a funcionalidade do menu lateral e responsividade
  */

// Namespace para evitar conflitos
var BPMLayout = BPMLayout || {};

BPMLayout = {


    // Variáveis de controle
    sidebarVisible: true,
    isMobile: false,

    // Inicialização
    init: function () {
        this.bindEvents();
        this.checkScreenSize();
        this.initializeLayout();
        this.addSmoothAnimations(); // Garante animações suaves

        // Listener para redimensionamento da tela
        window.addEventListener('resize', this.handleResize.bind(this));

        // Garante overlay correto ao iniciar
        this.updateSidebarState();

        // Garante que o botão hambúrguer tenha efeito visual
        const menuButton = document.querySelector('.layout-topbar-button');
        if (menuButton) {
            menuButton.addEventListener('mousedown', function() {
                menuButton.classList.add('pressed');
            });
            menuButton.addEventListener('mouseup', function() {
                menuButton.classList.remove('pressed');
            });
            menuButton.addEventListener('mouseleave', function() {
                menuButton.classList.remove('pressed');
            });
        }

        console.log('BPM Layout inicializado');
    },

    // Vincula eventos aos elementos
    bindEvents: function () {
        // Botão do menu hambúrguer
        const menuButton = document.querySelector('.layout-topbar-button');
        if (menuButton) {
            menuButton.addEventListener('click', this.toggleSidebar.bind(this));
        }

        // Clique fora do menu em dispositivos móveis
        document.addEventListener('click', this.handleOutsideClick.bind(this));

        // Tecla ESC para fechar menu
        document.addEventListener('keydown', this.handleKeyPress.bind(this));

        // Links do menu - adiciona efeito de ativo
        this.highlightActiveMenuItem();
    },

    // Verifica o tamanho da tela
    checkScreenSize: function () {
        this.isMobile = window.innerWidth <= 768;

        if (this.isMobile) {
            this.sidebarVisible = false;
        } else {
            this.sidebarVisible = true;
        }
    },

    // Inicializa o layout baseado no tamanho da tela
    initializeLayout: function () {
        const layoutWrapper = document.querySelector('.layout-wrapper');
        const sidebar = document.querySelector('.layout-sidebar');

        if (layoutWrapper && sidebar) {
            if (this.isMobile) {
                layoutWrapper.classList.add('layout-mobile');
                layoutWrapper.classList.remove('layout-sidebar-active');
            } else {
                layoutWrapper.classList.remove('layout-mobile');
                layoutWrapper.classList.add('layout-sidebar-active');
            }
        }
    },

    // Toggle do menu lateral
    toggleSidebar: function (event) {
        if (event) {
            event.preventDefault();
            event.stopPropagation();
        }

        this.sidebarVisible = !this.sidebarVisible;
        this.updateSidebarState();

        // Adiciona animação ao botão
        const button = document.querySelector('.layout-topbar-button');
        if (button) {
            button.classList.add('rotate');
            setTimeout(() => button.classList.remove('rotate'), 300);
        }
        this.debug('Sidebar toggled: ' + (this.sidebarVisible ? 'open' : 'closed'));
    },

    // Atualiza o estado visual do sidebar
    updateSidebarState: function () {
        const layoutWrapper = document.querySelector('.layout-wrapper');
        const sidebar = document.querySelector('.layout-sidebar');
        let overlay = document.querySelector('.layout-mask');
        if (!overlay) overlay = this.createOverlay();

        if (layoutWrapper && sidebar) {
            if (this.sidebarVisible) {
                layoutWrapper.classList.add('layout-sidebar-active');
                sidebar.setAttribute('aria-hidden', 'false');

                // Em mobile, adiciona overlay
                if (this.isMobile) {
                    if (!document.body.contains(overlay)) {
                        document.body.appendChild(overlay);
                    }
                    setTimeout(() => overlay.classList.add('active'), 10);
                } else {
                    if (overlay && overlay.parentNode) {
                        overlay.classList.remove('active');
                        setTimeout(() => {
                            if (overlay.parentNode) {
                                overlay.parentNode.removeChild(overlay);
                            }
                        }, 300);
                    }
                }
            } else {
                layoutWrapper.classList.remove('layout-sidebar-active');
                sidebar.setAttribute('aria-hidden', 'true');

                // Remove overlay
                if (overlay && overlay.parentNode) {
                    overlay.classList.remove('active');
                    setTimeout(() => {
                        if (overlay.parentNode) {
                            overlay.parentNode.removeChild(overlay);
                        }
                    }, 300);
                }
            }
        }
    },

    // Cria overlay para dispositivos móveis
    createOverlay: function () {
        const overlay = document.createElement('div');
        overlay.className = 'layout-mask';
        overlay.addEventListener('click', this.closeSidebar.bind(this));
        return overlay;
    },

    // Fecha o sidebar
    closeSidebar: function () {
        if (this.sidebarVisible) {
            this.sidebarVisible = false;
            this.updateSidebarState();
        }
    },

    // Manipula clique fora do menu
    handleOutsideClick: function (event) {
        if (!this.isMobile) return;

        const sidebar = document.querySelector('.layout-sidebar');
        const topbarButton = document.querySelector('.layout-topbar-button');

        if (this.sidebarVisible && sidebar && !sidebar.contains(event.target) &&
            !topbarButton.contains(event.target)) {
            this.closeSidebar();
        }
    },

    // Manipula teclas pressionadas
    handleKeyPress: function (event) {
        // ESC fecha o menu
        if (event.key === 'Escape' && this.sidebarVisible) {
            this.closeSidebar();
        }
    },

    // Manipula redimensionamento da tela
    handleResize: function () {
        const wasMobile = this.isMobile;
        this.checkScreenSize();

        // Se mudou de mobile para desktop ou vice-versa
        if (wasMobile !== this.isMobile) {
            this.initializeLayout();
            this.updateSidebarState();
        }
    },

    // Destaca o item ativo do menu
    highlightActiveMenuItem: function () {
        const currentPath = window.location.pathname;
        const menuItems = document.querySelectorAll('.layout-sidebar .ui-menuitem-link');

        menuItems.forEach(item => {
            const link = item.getAttribute('href');
            const menuItem = item.closest('.ui-menuitem');

            if (menuItem) {
                menuItem.classList.remove('active');

                if (link && (currentPath.includes(link) ||
                    (link === '/dashboard' && currentPath === '/'))) {
                    menuItem.classList.add('active');
                }
            }
        });
    },

    // Adiciona animações suaves
    addSmoothAnimations: function () {
        const sidebar = document.querySelector('.layout-sidebar');
        const mainContent = document.querySelector('.layout-main-content');

        if (sidebar) {
            sidebar.style.transition = 'transform 0.3s ease-in-out';
        }

        if (mainContent) {
            mainContent.style.transition = 'margin-left 0.3s ease-in-out';
        }
    },

    // Utilitários para notificações (integração com PrimeFaces Growl)
    showMessage: function (severity, summary, detail) {
        if (window.PF && PF('messages')) {
            PF('messages').renderMessage({
                severity: severity,
                summary: summary,
                detail: detail
            });
        }
    },

    // Método para mostrar loading
    showLoading: function () {
        const loadingEl = document.createElement('div');
        loadingEl.className = 'layout-loading';
        loadingEl.innerHTML = '<div class="loading-spinner"></div>';
        document.body.appendChild(loadingEl);

        setTimeout(() => loadingEl.classList.add('active'), 10);
        return loadingEl;
    },

    // Remove loading
    hideLoading: function (loadingEl) {
        if (loadingEl) {
            loadingEl.classList.remove('active');
            setTimeout(() => {
                if (loadingEl.parentNode) {
                    loadingEl.parentNode.removeChild(loadingEl);
                }
            }, 300);
        }
    },

    // Método para debug
    debug: function (message) {
        if (console && typeof console.log === 'function') {
            console.log('[BPM Layout]', message);
        }
    }


};


// Auto-inicialização quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', function() { BPMLayout.init(); });

// Compatibilidade com PrimeFaces AJAX
if (window.jsf && jsf.ajax) {
    jsf.ajax.addOnEvent(function (data) {
        if (data.status === 'success') {
            // Reinicializa após requests AJAX
            setTimeout(function () {
                BPMLayout.checkScreenSize();
                BPMLayout.initializeLayout();
                BPMLayout.updateSidebarState();
                BPMLayout.highlightActiveMenuItem();
            }, 100);
        }
    });
}

// Exposição global
window.BPMLayout = BPMLayout;
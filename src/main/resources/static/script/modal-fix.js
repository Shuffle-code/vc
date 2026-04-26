// modal-fix.js - рабочая версия для всех браузеров
(function() {
    'use strict';

    let scrollPosition = 0;

    function fixLayout() {
        // Фиксируем футер
        const footer = document.querySelector('footer');
        if (footer) {
            footer.style.marginLeft = '0';
            footer.style.marginRight = '0';
            footer.style.transform = 'none';
        }

        // Фиксируем контейнеры
        const containers = document.querySelectorAll('.container-fluid, .container');
        containers.forEach(container => {
            container.style.transform = 'none';
            container.style.marginLeft = '0';
            container.style.marginRight = '0';
        });

        // Фиксируем навбар
        const navbar = document.querySelector('.navbar');
        if (navbar) {
            navbar.style.transform = 'none';
            navbar.style.marginLeft = '0';
            navbar.style.marginRight = '0';
        }
    }

    function restoreLayout() {
        // Восстанавливаем футер
        const footer = document.querySelector('footer');
        if (footer) {
            footer.style.marginLeft = '';
            footer.style.marginRight = '';
            footer.style.transform = '';
        }

        // Восстанавливаем контейнеры
        const containers = document.querySelectorAll('.container-fluid, .container');
        containers.forEach(container => {
            container.style.transform = '';
            container.style.marginLeft = '';
            container.style.marginRight = '';
        });

        // Восстанавливаем навбар
        const navbar = document.querySelector('.navbar');
        if (navbar) {
            navbar.style.transform = '';
            navbar.style.marginLeft = '';
            navbar.style.marginRight = '';
        }

        // Восстанавливаем скролл
        window.scrollTo(0, scrollPosition);
    }

    document.addEventListener('DOMContentLoaded', function() {
        const modals = document.querySelectorAll('.modal');

        modals.forEach(function(modal) {
            modal.addEventListener('show.bs.modal', function() {
                scrollPosition = window.scrollY;
                setTimeout(fixLayout, 10);
            });

            modal.addEventListener('shown.bs.modal', function() {
                fixLayout();
            });

            modal.addEventListener('hide.bs.modal', function() {
                restoreLayout();
            });

            modal.addEventListener('hidden.bs.modal', function() {
                restoreLayout();
                setTimeout(restoreLayout, 50);
            });
        });
    });
})();
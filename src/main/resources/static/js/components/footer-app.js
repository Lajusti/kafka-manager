Vue.component('footer-app', {
    data: function() {
        return {
            text1: 'Alejandro Lajusticia Delgado',
            text2: 'UI Kafka manager'
         }
    },
    template:   '<div class="footer-copyright">' +
                    '<div class="container">' +
                        '{{ text1 }}' +
                        '<span class="right">{{ text2 }}</span>' +
                    '</div>' +
                '</div>'
})
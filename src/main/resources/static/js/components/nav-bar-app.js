Vue.component('nav-bar-app', {
    data: function() {
        return {
            logo: 'Lajus Kafka manager',
            topics: {
                text: 'Topics',
                href: '/'
            },
            topic: {
                text: 'Topic'
            },
            connectors: {
                text: 'Connectors',
                href: '/connectors'
            }
        }
    },
    props: ['active'],
    mounted: function() {
        $(document).ready(function(){
            $('.sidenav').sidenav();
        });
    },
    template: '<div>' +
                  '<nav class="app-color">' +
                      '<div class="nav-wrapper">' +
                          '<a :href="topics.href" class="brand-logo">{{ logo }}</a>' +
                          '<a href="#" data-target="mobile-nav-bar" class="sidenav-trigger"><i class="material-icons">menu</i></a>' +
                          '<ul class="right hide-on-med-and-down">' +
                              '<li v-bind:class="{ active: active == \'topics\'}"><a :href="topics.href">{{ topics.text }}</a></li>' +
                              '<li class="active" v-if="active == \'topic\'"><a href="#">{{ topic.text }}</a></li>' +
                              '<li v-bind:class="{ active: active == \'connectors\'}"><a :href="connectors.href">{{ connectors.text }}</a></li>' +
                          '</ul>' +
                      '</div>' +
                  '</nav>' +
                  '<ul class="sidenav" id="mobile-nav-bar">' +
                      '<li><a v-bind:class="{ active: active == \'topics\'}" :href="topics.href">{{ topics.text }}</a></li>' +
                      '<li class="active" v-if="active == \'topic\'"><a href="#">{{ topic.text }}</a></li>' +
                      '<li><a v-bind:class="{ active: active == \'connectors\'}" :href="connectors.href">{{ connectors.text }}</a></li>' +
                  '</ul>' +
              '</div>'
})
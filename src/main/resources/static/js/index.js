var main = new Vue({
    el: '#app',
    data: {
        topics: [],
        topic: {
            name: '',
            bootstrap: '',
            keySerializer: -1,
            valueSerializer: -1,
            schemaRegistry: '',
            id: 0
        },
        filter: '',
        waiting: true
    },
    computed: {
        filterTopics: function() {
            if (this.filter == '')
                return this.topics;

            var topics = [];
            for (index in this.topics) {
                var topic = this.topics[index];
                if (topic.name.indexOf(this.filter) > -1)
                    topics.push(topic);
            }

            return topics;
        },
        records: function() {
            var records = 0;
            for (index in this.topics) {
                var topic = this.topics[index];
                records += topic.records;
            }
            return records;
        }
    },
    watch: {
        filterTopics: function() {
            setTimeout(function(){
                $('.collapsible').collapsible();
            }, 20);
        }
    },
    created: function() {

        var self = this;
        self.getTopics(true);

        setInterval(function() {
            self.getTopics(false);
        }, 2000);

        $(document).ready(function(){
            $('.modal').modal();
        });

    },
    methods: {
        getTopics(removeWaiting) {
            var self = this;
            $.ajax({
                url: '/api/topics',
                type: 'GET',
                contentType: "application/json;charset=UTF-8",
                success: function(data) {
                    self.topics = data;
                    $(document).ready(function(){
                        $('.collapsible').collapsible();
                    });
                    if (removeWaiting)
                        self.waiting = false;
                }
            });
        },
        openNewTopicModal() {
            this.topic.name = '';
            this.topic.bootstrap = '';
            this.topic.keySerializer = -1;
            this.topic.valueSerializer = -1;
            this.topic.id = 0;
            this.topic.schemaRegistry = '';
            this.openModal();
        },
        addTopic() {
            if (this.topic.name != '' && this.topic.bootstrap != '' &&
                    this.topic.keySerializer > -1 && this.topic.valueSerializer > -1) {
                var self = this;
                self.waiting = true;
                var topic = {
                    name: self.topic.name,
                    bootstrap: self.topic.bootstrap,
                    keySerializer: self.topic.keySerializer,
                    valueSerializer: self.topic.valueSerializer,
                    schemaRegistry: self.topic.schemaRegistry
                }
                $.ajax({
                    url: '/api/topics',
                    type: 'POST',
                    contentType: "application/json;charset=UTF-8",
                    data: JSON.stringify(topic),
                    success: function(data) {
                        self.waiting = false;
                        $('#modalTopic').modal('close');
                        self.getTopics(false);
                    },
                    error: function() {
                        self.waiting = false;
                        $('#modalTopic').modal('close');
                        self.getTopics(false);
                    }
                })
            }
        },
        updateTopic() {
            if (this.topic.name != '' && this.topic.bootstrap != '' &&
                    this.topic.keySerializer > -1 && this.topic.valueSerializer > -1) {
                var self = this;
                self.waiting = true;
                var topic = {
                    name: self.topic.name,
                    bootstrap: self.topic.bootstrap,
                    keySerializer: self.topic.keySerializer,
                    valueSerializer: self.topic.valueSerializer,
                    schemaRegistry: self.topic.schemaRegistry
                }
                $.ajax({
                    url: '/api/topics/' + self.topic.id,
                    type: 'POST',
                    contentType: "application/json;charset=UTF-8",
                    data: JSON.stringify(topic),
                    success: function(data) {
                        self.waiting = false;
                        $('#modalTopic').modal('close');
                        self.getTopics(false);
                    },
                    error: function() {
                        self.waiting = false;
                        $('#modalTopic').modal('close');
                        self.getTopics(false);
                    }
                })
            }
        },
        removeTopic(index) {
            var self = this;
            self.waiting = true;
            $.ajax({
                url: '/api/topics/' + self.filterTopics[index].id,
                type: 'DELETE',
                success: function(data) {
                    self.waiting = false;
                    self.getTopics(false);
                },
                error: function() {
                    self.waiting = false;
                }
            })
        },
        openUpdateModal(index) {
            this.topic.name = this.filterTopics[index].name;
            this.topic.bootstrap = this.filterTopics[index].bootstrap;
            this.topic.id = this.filterTopics[index].id;
            this.topic.keySerializer = this.filterTopics[index].keySerializer;
            this.topic.valueSerializer = this.filterTopics[index].valueSerializer;
            this.topic.schemaRegistry = this.filterTopics[index].schemaRegistry;
            this.openModal();
        },
        openModal() {
            try {
                $('#modalTopic').modal('open');
            } catch(err) {
                $('#modalTopic').modal();
                $('#modalTopic').modal('open');
            }
        }
    }
})
var IDLE = 'idle';
var UP = 'up';
var PAUSED = 'paused';
var DOWN = 'down';
var KAFKA_PAUSED = 'PAUSED';

var main = new Vue({
    el: '#app',
    data: {
        selectedConnector: {
            name: '',
            status: '',
            response: {
                name: '',
                type: '',
                connector: {
                    state: '',
                    worker_id: ''
                },
                tasks: []
            }
        },
        connectors: [],
        newConnector: {
            name: '',
            url: ''
        },
        config: [],
        showNewConfig: false,
        newConfig: {
            name: '',
            value: ''
        },
        filter: '',
        waiting: true
    },
    computed: {
        filteredConnectors: function() {
            if (this.filter == '')
                return this.connectors;

            var connectors = [];
            for (index in this.connectors) {
                var connector = this.connectors[index];
                if (connector.name.indexOf(this.filter) > -1)
                    connectors.push(connector);
            }

            return connectors;
        },
        connectorsUp: function() {
            return this.countStatus(UP);
        },
        connectorsPaused: function() {
            return this.countStatus(PAUSED);
        },
        connectorsIdle: function() {
            return this.countStatus(IDLE);
        },
        connectorsDown: function() {
            return this.countStatus(DOWN);
        }
    },
    watch: {
        filteredConnectors: function() {
            setTimeout(function(){
                $('.collapsible').collapsible();
            }, 20);
        }
    },
    methods: {
        countStatus(status) {
            var count = 0;
            for (index in this.connectors) {
                var connector = this.connectors[index];
                if (connector.status == status)
                    count++;
            }
            return count;
        },
        openModal() {
            this.newConnector.name = '';
            this.newConnector.url = '';
            try {
                $('#newConnectorModal').modal('open');
            } catch(err) {
                $('#newConnectorModal').modal();
                $('#newConnectorModal').modal('open');
            }
        },
        addConnector() {
            if (this.newConnector.name != '' && this.newConnector.url != '') {
                var self = this;
                self.waiting = true;
                $.ajax({
                    url: '/api/connectors',
                    type: 'POST',
                    data: {
                        name: self.newConnector.name,
                        url: self.newConnector.url
                    },
                    success: function(data) {
                        self.addConnectorFromData(data);
                        self.waiting = false;
                    }, error: function(data) {
                        self.waiting = false;
                    }
                });
            }
            $('#newConnectorModal').modal('close');
            this.newConnector.name = '';
            this.newConnector.url = '';
        },
        openInfo(id) {
            this.hideProperties();
            this.selectedConnector = this.connectors[this.findPositionFromId(id)];
            $('#infoModal').modal('open');
        },
        remove(id) {
            var self = this;
            self.waiting = true;
            $.ajax({
                url: '/api/connectors/' + id,
                type: 'DELETE',
                success: function(data) {
                    var positionOfId = self.findPositionFromId(id);
                    self.connectors.splice(positionOfId, 1);
                    self.waiting = false;
                }, error: function(data) {
                    self.waiting = false;
                }
            });
        },
        findPositionFromId(id) {
            for (i in this.connectors) {
                if (this.connectors[i].id == id)
                return i;
            }
        },
        updateStatus() {
            for (i in this.connectors)
                this.updateStatusFromId(this.connectors[i].id);
        },
        updateStatusFromId(id) {
            var positionOfId = this.findPositionFromId(id);
            var connector = this.connectors[positionOfId];
            var self = this;
            $.ajax({
                url: '/api/connectors/' + connector.id + '/status',
                type: 'GET',
                success: function(data) {
                    self.waiting = false;
                    if (data == '')
                        connector.status = DOWN;
                    else {
                        if (typeof data.name === "undefined")
                            connector.status = DOWN;
                        else {
                            connector.response = data;
                            if (data.connector.state == KAFKA_PAUSED)
                                connector.status = PAUSED;
                            else
                                connector.status = UP;
                        }
                    }
                },
                error: function(data) {
                    connector.status = DOWN;
                    self.waiting = false;
                }
            });
        },
        showProperties(index) {
            var id = this.filteredConnectors[index].id;

            this.showNewConfig = false;
            this.newConfig.name = '';
            this.newConfig.value = '';

            for (i in this.connectors)
                this.connectors[i].showConfig = false;

            var self = this;
            self.waiting = true;

            $.ajax({
                url: '/api/connectors/' + id + '/config',
                type: 'GET',
                success: function(data) {
                    if (data != '') {
                        if (typeof data.config !== "undefined") {
                            self.config = [];
                            for (prop in data.config) {
                                self.config.push({
                                    name: prop,
                                    value: data.config[prop]
                                });
                            }
                            self.hideNewProperty();
                            self.filteredConnectors[index].showConfig = true;
                        }
                    }
                    self.waiting = false;
                }, error: function(data) {
                    self.waiting = false;
                }
            });
        },
        updateConfig(index) {
            this.filteredConnectors[index].showUpdate = true;
        },
        hideProperties(index){
            this.filteredConnectors[index].showConfig = false;
            this.filteredConnectors[index].showUpdate = false;
            this.config = [];
        },
        sendConfig(index) {
            var id = this.filteredConnectors[index].id;
            var self = this;
            var config = [];
            for (i in self.config) {
                var param = self.config[i];
                config.push({
                    name: param.name,
                    value: param.value
                });
            }
            var configToSend = {
                config: self.config
            };
            self.waiting = true;
            $.ajax({
                url: '/api/connectors/' + id + '/config',
                type: 'POST',
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify(configToSend),
                success: function(data) {
                    self.hideProperties(index);
                    self.waiting = false;
                },
                error: function(data) {
                    self.hideProperties(index);
                    self.waiting = false;
                }
            });
        }, addConnectorFromData(data) {
            var newConnector = data;
            newConnector.status = IDLE;
            newConnector.response = {
                name:'',
                type:''
            };
            newConnector.showConfig = false;
            newConnector.showUpdate = false;
            this.connectors.push(newConnector);
            this.updateStatusFromId(newConnector.id);
        }, removeProperty(index) {
            this.config.splice(index, 1);
        }, addProperty() {
            if (this.newConfig.value != '' && this.newConfig.name != '') {
                this.config.push({
                    name: this.newConfig.name,
                    value: this.newConfig.value
                });
                this.hideNewProperty();
            }
        }, hideNewProperty() {
            this.newConfig.name = '';
            this.newConfig.value = '';
            this.showNewConfig = false;
        }, resume(id) {
            this.callPost('/api/connectors/' + id + '/resume', id);
        }, pause(id) {
            this.callPost('/api/connectors/' + id + '/pause', id);
        }, restart(id) {
            this.callPost('/api/connectors/' + id + '/restart', id);
        }, callPost(url, idConnector) {
            var self = this;
            self.waiting = true;
            $.ajax({
                url: url,
                type: 'POST',
                success: function(data) {
                    setTimeout(
                        function() {
                            self.updateStatusFromId(idConnector)
                        },
                        3000
                    );
                },
                error: function(data) {
                    self.waiting = false;
                }
            });
        }, restartTask(idConnector, idTask) {
            var self = this;
            self.waiting = true;
            $.ajax({
                url: '/api/connectors/' + idConnector + '/tasks/' + idTask + '/restart',
                type: 'POST',
                success: function(data) {
                    self.waiting = false;
                },
                error: function(data) {
                    self.waiting = false;
                }
            });
        }
    },
    created: function () {
        var self = this;

        $(document).ready(function(){
            $('.modal').modal();
        });

        $.ajax({
            url: '/api/connectors',
            type: 'GET',
            success: function(data) {
                for (i in data)
                    self.addConnectorFromData(data[i]);

                setInterval(self.updateStatus, 10000);
                self.waiting = false;
            }, error: function(data) {
                self.waiting = false;
            }
        });

    }
});
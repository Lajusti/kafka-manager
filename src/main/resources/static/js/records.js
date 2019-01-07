var main = new Vue({
    el: '#app',
    data: {
        href: '',
        topic: {
            name: ''
        },
        records: [],
        filter: '',
        selectedFilter: '',
        numRecords: 0,
        currentRecords: 0,
        initial: true,
        page: 0,
        maxPages: 0,
        waiting: true
    },
    computed: {
        hasPreviousPage: function() {
            return this.page - 1 > 0;
        },
        has2PreviousPages: function() {
            return this.page - 2 > 0;
        },
        hasToShowFirstPage: function() {
            return this.page > 0;
        },
        hasMorePreviousPages: function() {
            return this.page - 3 > 0;
        },
        isFirstPage: function() {
            return this.page == 0;
        },
        hasNextPage: function() {
            return this.page + 2 < this.maxPages;
        },
        has2NextPages: function() {
            return this.page + 3 < this.maxPages;
        },
        hasToShowLastPage: function() {
            return this.page < this.maxPages - 1;
        },
        hasMoreNextPages: function() {
            return this.page + 4 < this.maxPages;
        },
        isLastPage: function() {
            return this.page == this.maxPages;
        }
    },
    created: function() {
        this.href = window.location.pathname;
        var self = this;
        self.getRecords(0);

    },
    methods: {
        goToPage(page) {
            this.page = page;
            this.waiting = true;
            this.getRecords();
        },
        goToFirstPage: function() {
            this.goToPage(0);
        },
        goTo2PreviousPage: function() {
            this.goToPage(this.page - 2);
        },
        goToPreviousPage: function() {
            this.goToPage(this.page - 1);
        },
        goToNextPage: function() {
            this.goToPage(this.page + 1);
        },
        goTo2NextPage: function() {
            this.goToPage(this.page + 2);
        },
        goToLastPage: function() {
            this.goToPage(this.maxPages - 1);
        },
        getRecords: function() {
            var self = this;
            var filter = '';
            if (this.selectedFilter != '')
                filter = '&filter=' + this.selectedFilter;
            $.ajax({
                url: '/api/' + self.href + '/records?size=10&page=' + self.page + filter,
                type: 'GET',
                contentType: "application/json;charset=UTF-8",
                success: function(data) {
                    if (data.loadingRecords) {
                        setTimeout(function(){
                            self.getRecords();
                        }, 500)
                    } else
                        self.setData(data);
                }
            });
        },
        addRecords: function(records) {
            for (index in records) {
                var record = records[index];
                if (record.key != null)
                    record.key = this.formatJson(record.key);
                record.value = this.formatJson(record.value);
                this.records.push(record);
            }
            $(document).ready(function(){
                $('.collapsible').collapsible();
            });
        },
        formatJson: function(value) {
            try {
                var json = JSON.parse(value);
                return JSON.stringify(json, undefined, 2);
            } catch (err) {
                return value;
            }
        },
        updateRecords: function() {
            var self = this;
            self.waiting = true;
            $.ajax({
                url: '/api/' + self.href + '/records?size=10',
                type: 'PUT',
                contentType: "application/json;charset=UTF-8",
                success: function(data) {
                    self.filter = '';
                    self.selectedFilter = '';
                    if (data.loadingRecords) {
                        setTimeout(function(){
                            self.getRecords();
                        }, 500)
                    } else
                        self.setData(data);
                }
            });
        },
        setData: function(data) {
            this.records = [];
            this.topic = data.topic;
            if (this.initial) {
                this.numRecords = data.numRecords;
                this.initial = false;
            }
            this.currentRecords = data.numRecords;
            this.addRecords(data.records);
            this.waiting = false;
            this.page = data.page;
            this.maxPages = data.maxPages;
        },
        filterRecords: function() {
            this.selectedFilter = this.filter;
            this.goToFirstPage();
        },
        eraseFilter: function() {
            this.selectedFilter = '';
            this.goToFirstPage();
        }
    }
})
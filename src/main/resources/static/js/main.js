function getIndex(list, id) {
    for (var i = 0; i < list.length; i++) {
        if (list[i].id === id) {
            return i;
        }
    }
    return -1;
}

var stockApi = Vue.resource('/stock{/id}');

Vue.component('search-form', {
    props: ['stocks', 'errors', 'pagesParams'],
    data: function () {
        return {
            code: '',
            status: '',
            pageNum: '',
            pageSize: ''
        }
    },
    template:
        '<div>' +
        '<h4>Пошук</h4>' +
        '<div class="form-group mb-2">' +
        '<label for="search-code">ЄДРПОУ</label>' +
        '<input id="search-code" class="form-control" type="number" minlength="8" maxlength="8" ' +
        'placeholder="ЄДРПОУ" name="code" v-model="code" />' +
        '</div>' +
        '<h5>Статус</h5>' +
        '<div class="form-check mb-2">' +
        '  <input class="form-check-input" type="radio" name="status" id="search-active" value="ACTIVE" ' +
        '       v-model="status" />' +
        '  <label class="form-check-label" for="search-active">Active</label>' +
        '</div>' +
        '<div class="form-check mb-2">' +
        '  <input class="form-check-input" type="radio" name="status" id="search-deleted" value="DELETED" ' +
        '       v-model="status" />' +
        '  <label class="form-check-label" for="search-deleted">Deleted</label>' +
        '</div>' +
        '<div class="form-check mb-2">' +
        '  <input class="form-check-input" type="radio" name="status" id="search-all" value="" ' +
        '       v-model="status" checked>' +
        '  <label class="form-check-label" for="search-all">Всі</label>' +
        '</div>' +
        '<div class="form-group mb-2">' +
        '    <label for="search-pageNum">Сторінка</label>' +
        '<div v-if="pagesParams.current">' +
        '  <p>( {{ pagesParams.current }} з {{ pagesParams.total }} )</p>' +
        '</div>' +
        '    <input type="number" min="1" class="form-control" id="search-pageNum" placeholder="Сторінка" ' +
        '       name="pageNum" v-model="pageNum"/>' +
        '</div>' +
        '<div class="form-group mb-2">' +
        '    <label for="search-pageSize">Кількість записів</label>' +
        '    <select class="form-control" id="search-pageSize" name="pageSize" v-model="pageSize">' +
        '      <option>10</option>' +
        '      <option>25</option>' +
        '      <option>50</option>' +
        '      <option>100</option>' +
        '    </select>' +
        '</div>' +
        '<button type="submit" class="btn btn-primary mb-2" value="Знайти" @click="find">Знайти</button>' +
        '</div>',
    methods: {
        find: function () {
            this.stocks.length = 0;
            this.errors.length = 0;
            var searchParams = {
                code: this.code,
                status: this.status,
                pageNum: this.pageNum,
                pageSize: this.pageSize
            }
            stockApi.get(searchParams)
                .then(result => {
                        result.json().then(data =>
                            data.forEach(stock => this.stocks.push(stock)));
                        this.pagesParams.current = result.headers.get('currentpage');
                        this.pagesParams.total = result.headers.get('totalpages');
                    },
                    result => result.body.errors.forEach(error => this.errors.push(error))
                );
        }
    }
})

Vue.component('stock-form', {
    props: ['stocks', 'stockAttr', 'errors'],
    data: function () {
        return {
            id: '',
            code: '',
            amount: '',
            faceValue: '',
            date: '',
            comment: '',
            status: ''
        }
    },
    watch: {
        stockAttr: function (newVal, oldVal) {
            this.id = newVal.id;
            this.code = newVal.code;
            this.amount = newVal.amount;
            this.faceValue = newVal.faceValue;
            this.date = newVal.date;
            this.comment = newVal.comment;
            this.status = newVal.status
        }
    },
    template:
        '<div class="bg-light">' +
        '<h4>Додати/редагувати</h4>' +
        '<div class="form-group mb-2">' +
        '   <label for="add-code">ЄДРПОУ</label>' +
        '   <input id="add-code" class="form-control" type="number" minlength="8" maxlength="8" ' +
        '       placeholder="ЄДРПОУ" v-model="code" />' +
        '</div>' +
        '<div class="form-group mb-2">' +
        '   <label for="add-amount">Кількість</label>' +
        '   <input id="add-amount" class="form-control" type="number" min="0" ' +
        '       placeholder="Кількість" v-model="amount" />' +
        '</div>' +
        '<div class="form-group mb-2">' +
        '   <label for="add-faceValue">Номінальна вартість</label>' +
        '   <input id="add-faceValue" class="form-control" type="number" min="0" ' +
        '       placeholder="Номінальна вартість" v-model="faceValue" />' +
        '</div>' +
        '<div class="form-group mb-2">' +
        '   <label for="add-date">Дата випуску</label>' +
        '   <input id="add-date" class="form-control" type="date" ' +
        '       placeholder="Дата випуску" v-model="date" />' +
        '</div>' +
        '<div class="form-group mb-2">' +
        '   <label for="add-comment">Коментар</label>' +
        '   <textarea id="add-comment" class="form-control" type="date" ' +
        '       placeholder="Коментар" v-model="comment"></textarea>' +
        '</div>' +
        '<button type="submit" class="btn btn-success mb-2" value="Зберегти" @click="save">Зберегти</button>' +
        '</div>',
    methods: {
        save: function () {
            this.errors.length = 0;
            var stock = {
                code: this.code,
                amount: this.amount,
                faceValue: this.faceValue,
                date: this.date,
                comment: this.comment,
                status: this.status
            };

            if (this.id) {
                stockApi.update({id: this.id}, stock).then(result =>
                        result.json().then(data => {
                            var index = getIndex(this.stocks, data.id);
                            this.stocks.splice(index, 1, data);
                            this.code = '';
                            this.amount = '';
                            this.faceValue = '';
                            this.date = '';
                            this.comment = ''
                        }),
                    result => result.body.errors.forEach(error => this.errors.push(error)))
            } else {
                stockApi.save({}, stock).then(result =>
                        result.json().then(data => {
                            this.stocks.push(data);
                            this.code = '';
                            this.amount = '';
                            this.faceValue = '';
                            this.date = '';
                            this.comment = ''
                        }),
                    result => {
                        result.body.errors.forEach(error => this.errors.push(error))
                    }
                )
            }
        }
    }
})

Vue.component('stock-row', {
    props: ['stock', 'editMethod', 'stocks', 'errors'],
    template: '<tr>' +
        '<td>{{stock.id}}</td>' +
        '<td>{{stock.code}}</td>' +
        '<td>{{stock.amount}}</td>' +
        '<td>{{stock.faceValue}}</td>' +
        '<td>{{stock.totalFaceValue}}</td>' +
        '<td>{{stock.date}}</td>' +
        '<td>{{stock.comment}}</td>' +
        '<td>{{stock.status}}</td>' +
        '<td><button type="submit" class="btn btn-primary" value="Ред" @click="edit">Ред</button></td>' +
        '<td><button type="submit" class="btn btn-danger" value="X" @click="del">X</button></td>' +
        '</tr>',
    methods: {
        edit: function () {
            this.editMethod(this.stock);
        },
        del: function () {
            this.errors.length = 0;
            stockApi.delete({id: this.stock.id}).then(result =>
                    result.json().then(data => {
                        var index = getIndex(this.stocks, data.id);
                        this.stocks.splice(index, 1, data);
                    }),
                result => result.body.errors.forEach(error => this.errors.push(error))
            )
        }
    }
})

Vue.component('error-message', {
    props: ['error'],
    template: '<div>' +
        '<div class="alert alert-danger" role="alert">{{ error }}</div>' +
        '</div>'
})

Vue.component('stocks-list', {
    props: ['stocks', 'errors', 'pagesParams'],
    data: function () {
        return {
            stock: null
        }
    },
    template:
        '<div>' +
        ' <div class="container">' +
        '   <div class="row">' +
        '     <div class="col">' +
        '      <search-form :stocks="stocks" :errors="errors" :pagesParams="pagesParams"/>' +
        '     </div>' +
        '     <div class="col">' +
        '      <stock-form :stocks="stocks" :errors="errors" :stockAttr="stock" />' +
        '     </div>' +
        '   </div>' +
        '   <div v-if="errors.length > 0">' +
        '    <div class="row">' +
        '     <div class="col">' +
        '      <error-message v-for="error in errors" :key="error" :error="error" />' +
        '     </div>' +
        '    </div>' +
        '   </div>' +
        '   <div class="row">' +
        '     <div class="col">' +
        '      <table class="table table-bordered">' +
        '       <thead><tr><th>ID</th><th>ЄДРПОУ</th><th>Кількість</th><th>Номінальна вартість</th>' +
        '         <th>Загальна номінальна вартість</th><th>Дата випуску</th><th>Коментар</th>' +
        '         <th>Статус</th><th></th><th></th></tr>' +
        '       </thead>' +
        '       <stock-row v-for="stock in stocks" :key="stock.id" :stock="stock" :errors="errors" :editMethod="editMethod" ' +
        '          :stocks="stocks" />' +
        '      </table>' +
        ' </div></div></div>' +
        '</div>',
    created: function () {
        stockApi.get().then(result => {
                result.json().then(data =>
                    data.forEach(stock => this.stocks.push(stock)));
                this.pagesParams.current = result.headers.get('currentpage');
                this.pagesParams.total = result.headers.get('totalpages');
            },
            result => result.body.errors.forEach(error => this.errors.push(error))
        )
    },
    methods: {
        editMethod: function (stock) {
            this.stock = stock;
        }

    }
})

var app = new Vue({
    el: '#app',
    template: '<div>' +
        '<stocks-list :stocks="stocks" :errors="errors" :pagesParams="pagesParams"/>' +
        '</div>',
    data: {
        stocks: [],
        errors: [],
        pagesParams: {
            current: '',
            total: ''
        }
    }
})
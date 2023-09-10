Vue.createApp({
    data() {
        return {
            clientInfo: {},
            creditCards: [],
            debitCards: [],
            errorToats: null,
            errorMsg: null,
            cardToDeleteId: null,
        }
    },
    methods: {
        getData: function () {
            axios.get("/api/clients/current")
                .then((response) => {
                    //get client ifo
                    this.clientInfo = response.data;
                    this.creditCards = this.clientInfo.cards.filter(card => card.type == "CREDIT");
                    this.debitCards = this.clientInfo.cards.filter(card => card.type == "DEBIT");
                })
                .catch((error) => {
                    this.errorMsg = "Error getting data";
                    this.errorToats.show();
                })
        },
        formatDate: function (date) {
            return new Date(date).toLocaleDateString('en-gb');
        },
        isDateExpired: function (date) {
            const currentDate = new Date();
            const cardThruDate = new Date(date);
            return cardThruDate < currentDate;
        },
        signOut: function () {
            axios.post('/api/logout')
                .then(response => window.location.href = "/web/index.html")
                .catch(() => {
                    this.errorMsg = "Sign out failed"
                    this.errorToats.show();
                })
        },
        deleteCard: function (cardId) {
            this.cardToDeleteId = cardId;
            this.modal.show();
        },        
        confirmDelete: function (cardId) {
            console.log("Confirm Delete - Card ID: ", cardId);
    
            axios.delete(`/api/clients/current/cards/${cardId}`)
                .then(response => {
                    console.log("Card deleted successfully.");
                    this.modal.hide();
                    window.location.href = "/web/cards.html";
                })
                .catch((error) => {
                    console.log(error);
                    this.errorMsg = error.response.data;
                    this.errorToats.show();
                });
        }        
    },
    mounted: function () {
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.modal = new bootstrap.Modal(document.getElementById('confirModal'));
        this.getData();
    }
}).mount('#app')
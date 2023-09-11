Vue.createApp({

    data() {
        return {
            errorToats: null,
            errorMsg: null,
            number: "",
            cvv: 0,
            amount: 0,
            description: "",
        }
    },
    methods: {
        signOut: function () {
            axios.post('/api/logout')
                .then(response => window.location.href = "/web/index.html")
                .catch(() => {
                    this.errorMsg = "Sign out failed"
                    this.errorToats.show();
                })
        },
        create: function (event) {
            event.preventDefault();
            if (this.number.trim() === "") {
                this.errorMsg = "Card number cannot be empty";
                this.errorToats.show();
            } else if (this.cvv <= 0) {
                this.errorMsg = "CVV must be greater than 0";
                this.errorToats.show();
            } else if (this.amount <= 0) {
                this.errorMsg = "Amount must be greater than 0";
                this.errorToats.show();
            } else if (this.description.trim() === "") {
                this.errorMsg = "Description cannot be empty";
                this.errorToats.show();
            } else {
                let config = {
                    headers: {
                        'content-type': 'application/x-www-form-urlencoded'
                    }
                }
                axios.post(`/api/payments/?number=${this.number}&cvv=${this.cvv}&amount=${this.amount}&description=${this.description}`,config)
                    .then(response => {
                        console.log(response.data);
                    })
                    .catch(error => {
                        this.errorMsg = error.response.data;
                        this.errorToats.show();
                    });
            }
        }      
    },
    mounted: function () {
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
    }
}).mount('#app')

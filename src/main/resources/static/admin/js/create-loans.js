Vue.createApp({

    data() {
        return {
            errorToats: null,
            errorMsg: null,
            successToats: null,
            successMsg: null,
            loanName: "",
            loanMaxAmount: 0,
            loanPercentage: 0,
            loanPayments: [],
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
            if (this.loanName.trim() === "") {
                this.errorMsg = "Name cannot be empty";
                this.errorToats.show();
            } else if (this.loanMaxAmount <= 0) {
                this.errorMsg = "Max amount must be greater than 0";
                this.errorToats.show();
            } else if (this.loanPercentage <= 0) {
                this.errorMsg = "Percentage must be greater than 0";
                this.errorToats.show();
            } else if (this.loanPayments.length === 0) {
                this.errorMsg = "Select at least one payment option";
                this.errorToats.show();
            } else {
                let config = {
                    headers: {
                        'content-type': 'application/x-www-form-urlencoded'
                    }
                }
                axios.post(`/api/loans/create?name=${this.loanName}&maxAmount=${this.loanMaxAmount}&percentage=${this.loanPercentage}&payments=${this.loanPayments}`,config)
                    .then(response => {
                        this.successMsg = "Loan created successfully"
                        this.successToats.show();
                    })
                    .catch(error => {
                        this.errorMsg = error.response.data;
                        this.errorToats.show();
                    })
            }
        }      
    },
    mounted: function () {
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.successToats = new bootstrap.Toast(document.getElementById('success-toast'));
    }
}).mount('#app')

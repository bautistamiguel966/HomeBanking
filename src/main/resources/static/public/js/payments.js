Vue.createApp({
    data() {
        return {
            errorToast: null,
            errorMsg: null,
            paymentDTO: {
                number: "",
                cvv: 0,
                amount: 0,
                description: ""
            }
        }
    },
    methods: {
        create: function (event) {
            event.preventDefault();
            if (this.paymentDTO.number.trim() === "") {
                this.errorMsg = "Card number cannot be empty";
                this.errorToast.show();
            } else if (this.paymentDTO.cvv <= 0) {
                this.errorMsg = "CVV must be greater than 0";
                this.errorToast.show();
            } else if (this.paymentDTO.amount <= 0) {
                this.errorMsg = "Amount must be greater than 0";
                this.errorToast.show();
            } else if (this.paymentDTO.description.trim() === "") {
                this.errorMsg = "Description cannot be empty";
                this.errorToast.show();
            } else {
                axios.post('http://localhost:8080/api/payments', this.paymentDTO)
                    .then(response => {
                        console.log(response.data);
                    })
                    .catch(error => {
                        this.errorMsg = error.response.data;
                        this.errorToast.show();
                    });
            }
        }
    },
    mounted: function () {
        this.errorToast = new bootstrap.Toast(document.getElementById('danger-toast'));
    }
}).mount('#app')


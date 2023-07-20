import { setCalendar } from './calendar.js';
var text = document.querySelector("#serviceId");
var serviceId = text.textContent;
var images = [];
var cImgPos = 0;

const ESCAPE_HTML_MAP = new Map([
    ['&', '&amp;'],
    ['<', '&lt;'],
    ['>', '&gt;'],
    ['"', '&quot;'],
    ["'", '&#039;']
]);

const ESCAPE_HTML_REGEX = /[&<>"']/g;

function escapeHtml(text) {
    return String(text).replace(ESCAPE_HTML_REGEX, (m) => ESCAPE_HTML_MAP.get(m));
}

export function setServiceID(serviceid){
    serviceId = serviceid;
}


$(document).ready(function () {
	fetch(`/api/services/id/${serviceId}`)
        .then(response => response.json())
        .then(data => {
            //service info
            $('#service-model').text(escapeHtml(data.model));
            $('#service-tag').text(escapeHtml(data.tag));
            $('#service-description').text(escapeHtml(data.description));
		    $('#service-partcost').text('R$ ' + escapeHtml(data.partCost.toLocaleString('pt-BR', {minimumFractionDigits: 2})));
            $('#service-labortax').text('R$ ' + escapeHtml(data.laborTax.toLocaleString('pt-BR', {minimumFractionDigits: 2})));
            $('#service-discount').text(escapeHtml(data.discount) + '%');
            $('#service-price').text('R$ ' + escapeHtml(data.payed.toLocaleString('pt-BR', {minimumFractionDigits: 2})));
            $('#service-finalprice').text('R$ ' + escapeHtml(data.finalPrice.toLocaleString('pt-BR', {minimumFractionDigits: 2})));
            let payway = new Array("Dinheiro", "Pix", "Débito", "Crédito");
            $('#service-method').text(escapeHtml(payway[data.payway]));

            $('#service-client').text(escapeHtml(data.customerName));
            $('#service-tech').text(escapeHtml(data.user.name));

            // Display service div by id
            $('#service-status').children().each(function (index) {
			  if (index==data.status){
				$(this).css('display','block');
			  }
			});

            // Calendar setup
            let datarimw = data.previsionTime;

            if (datarimw) {
                let prevDate = new Date(data.previsionTime);
                setCalendar(prevDate.getFullYear(), prevDate.getMonth() + 1, prevDate.getDate())
            } else {
                $('.container-calendar').remove();
            }
        })
        .catch(error => console.error(error));


    // Images setup

	fetch(`/api/services/img/filenames/${serviceId}`)
        .then(response => response.json())
        .then(data => {
            for (var i = 0; i < data.length; i++) {
                let eleimg = `<img id="img-current" src="/api/services/img/${serviceId}?filename=${data[i]}">`;
                images.push(eleimg);
            }
            if(images.length==0){
                $('#img-card').remove();
            }else{
                $('.carousel-container-img').html(images[cImgPos]);
                $('#carousel-count').text(`Imagens ${cImgPos + 1}/${images.length}`);
            }
        })
        .catch(function(){$('#img-card').remove();});


});

// img controller listener
$('#img-l').click(function () {
    if (cImgPos <= images.length && cImgPos >= 1) {
        cImgPos--;
        imgSetPos('l');
    }
});

$('#img-r').click(function () {
    if (cImgPos < images.length - 1) {
        cImgPos++;
        imgSetPos('r');
    }
});

function imgSetPos(action) {
    $('#carousel-count').text(`Imagens ${cImgPos + 1}/${images.length}`);
    if (action == 'l') {
        $('.carousel-container-img').html(images[cImgPos]);
    } else {
        $('.carousel-container-img').html(images[cImgPos]);
    }
}

$('.carousel-container-img').click(function () {
    $('#full-img-overlay').html(images[cImgPos]);
    $('#full-img-overlay').css('display', 'flex');
});

$('#full-img-overlay').click(function () {
    $('#full-img-overlay').css('display', 'none');
});


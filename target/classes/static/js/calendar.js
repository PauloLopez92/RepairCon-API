var setCalendar = function setCalendar(year, month, day){
    let firstDay = new Date(year, month - 1, 1);
    let nDays = new Date(year, month, 0).getDate();
    let dWeek = firstDay.toLocaleDateString('pt-BR', { weekday: 'long' });
    let dMonth = firstDay.toLocaleDateString('pt-BR', { month: 'long' });
    let pMonth = month - 1;
    let fday = firstDay.getDay();
    if(fday==0&&dWeek=="domingo"){
        fday = 7;
    }

    let pnDays = new Date(year, pMonth, 0).getDate()
    let weeks = $('.weeks').children();
    let cFistDay = fday;
    let cTotalDays = 1;


    $(".current-date h1").text((dWeek.charAt(0).toUpperCase() + dWeek.slice(1)) + ` ${day}`);
    $(".current-date h2").text((dMonth.charAt(0).toUpperCase() + dMonth.slice(1)) + ` ${year}`);

    weeks.each(function (index) {
        if (index == 0) {
            $(this).children().each(function (index) {
                if (index < fday - 1) {
                    $(this).addClass('last-month');
                    $(this).text(pnDays - cFistDay);
                    cFistDay--;
                } else {
                    if(cTotalDays==day){
                        $(this).addClass("active");
                    }
                    if (cTotalDays < nDays) {
                        $(this).text(cTotalDays);
                        cTotalDays++;
                    }
                }
            });
        } else {
            $(this).children().each(function (index) {
                if(cTotalDays==day){
                    $(this).addClass("active");
                }
                if (cTotalDays <= nDays) {
                    $(this).text(cTotalDays);
                    cTotalDays++;
                }
            });
        }
    });
}
export {setCalendar};

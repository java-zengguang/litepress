/**
 * Created by Administrator on 2018/12/18 0018.
 */




function submitTablePage(curPage, pageSize) {
    var form = $("#selectForm");
    url = form.attr("action");
    data = form.serialize();
    data = data + "&page.currentPage=" + curPage + "&page.pageSize=" + pageSize;
    type = form.attr("mathod");
    var target = $("#tableContent");
    dataType = "json";
    var page = submitAjaxPage(url, type, dataType, data, target, "table");
    return page;
}


//翻页按钮点击后触发的回调函数
function pageNavCallBack(clickPage) {
    var pageNavObj = null;
    var pageSize = 10;
    var page = submitTablePage(clickPage, pageSize);
    var pageCount = page.totalResultSize / pageSize + 1;
    var perPageNum = page.totalPageSize % 10;
    //根据新的数据重新生成
    pageNavObj = new PageNavCreate("PageNavId", {
        pageCount: pageCount,
        currentPage: clickPage,
        perPageNum: perPageNum,
    });
    pageNavObj.afterClick(pageNavCallBack);///最后还要再次绑定点击事件后的回调函数
}

function initTableHead(target) {
    array = new Array();
    array[0] = {TH: "*"};
    array[1] = {TH: "姓名"};
    array[2] = {TH: "序号"};
    array[3] = {TH: "时间"};
    createTH(array, target);
}


function deleteBatch() {
    url = "/table/deleteTableDate.do";
    tableName = $("#tableName").val();
    var text = getCheckbox();
    var param = "id=" + text + "&tableName=" + tableName;
    alert(param);
    submitAjax(url, "post", "json", param, "", "update");

}


function searchTableName() {
    url = "/SQLController/querySQL.do";
    var param = "sql=select table_name as tableName ,table_type as tableType from information_schema.tables " +
        "where 1=1 and table_schema='test' ";
    submitAjax(url, "post", "json", param, $("#tableNameList"), "dataList");

}

function initTH() {
    initTableHead($("#tableContent"));
}

function querySQL() {
    url = "/SQLController/querySQL.do";
    var param = "sql=select * from test " +
        "where 1=1 limit 0,10";
    initTableHead($("#tableContent"));
    submitAjax(url, "post", "json", param, $("#tableContent"), "tableData");

}





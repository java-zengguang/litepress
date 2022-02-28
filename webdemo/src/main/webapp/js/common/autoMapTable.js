/**
 * Created by Administrator on 2018/12/18 0018.
 */


function getCheckbox() {

    var text = $("input:checkbox[name='primaryKey']:checked").map(function (index, elem) {
        return $(elem).val();
    }).get().join(',');
    alert("选中的checkbox的值为：" + text);
    return text;

}


function autoFillList(result, target) {
    var str = "";
    if (result.success) {
        if (result.list.length > 0) {
            $.each(result.list, function (i, table) {

                str = str + "<option value='" + table.tableName + "'>" + table.tableName + "</option>";
            });
            console.log(str);
            target.empty().append(str);
        }
    } else {
        alert(result.message);
    }

}


function createTH(object, target) {
    if (target.find("thead").length == 0) {
       target.append("thead");
    }
    target =target.children("thead");
    target.html("");
    var tableHead = "<tr>";
    for (i = 0; i < object.length; i++) {
        TH = object[i].TH;
        tableHead = tableHead + "<th>" + TH + "</th>";
    }
    tableHead = tableHead + "</tr>"
    target.append(tableHead);
}

function autoFillTable(result, target) {
    target.html("");
    primaryKey = result.primaryKey;
    var thead = "";
    var tbody = "";
    if (result.success) {
        if (result.list.length > 0) {
            var tableHead = "<tr>";
            $.each(result.list, function (i, map) {
                if (i == 0) {
                    var thLine = "<tr><th>*</th>";
                }
                var line = "<tr><td> <input type='checkbox'name='primaryKey' value='" + map[primaryKey] + "' /></td>";
                $.each(map, function (key, value) {
                    if (i == 0) {
                        thLine = thLine + "<th>" + key + "</th>";
                    }
                    if (value == null || value == "null") {
                        value = "";
                    }
                    line = line + "<td>" + value + "</td>";
                });
                line = line + "</tr>";
                if (i == 0) {
                    thead = "<thead>" + thLine + "</tr></thead>";
                }
                tbody = tbody + line;
            });
        }
        target.append(thead);
        target.append("<tbody>" + tbody + "</tbody>");
        page = result.page;
    }
    return page;

}


function autoFillTableData(result, target) {
    //target.html("");
    if (target.find("tbody").length == 0) {
        target.append("<tbody></tbody>");
    }
    target = target.children("tbody");
    target.html("");
    primaryKey = result.primaryKey;
    if (result.success) {
        if (result.list.length > 0) {

            $.each(result.list, function (i, map) {

                var line = " <tr><td> <input type='checkbox'name='primaryKey' value='" + map[primaryKey] + "' /></td>";
                line = line;
                $.each(map, function (key, value) {
                    if (value == null || value == "null") {
                        value = "";
                    }
                    line = line + "<td>" + value + "</td>";
                });

                line = line + "</tr>";
                target.append(line);

            });

        }
    }


}


function autoUpdate(result) {
    if (result.success) {
        alert(result.message);
        window.location.reload();
    } else {
        alert(result.message);
    }
}

function autoFillData(result, target, targetType) {
    switch (targetType) {
        case "table": {
            autoFillTable(result, target);
            break;
        }
        case "tableData": {
            autoFillTableData(result, target);
            break;
        }
        case "dataList": {
            autoFillList(result, target);
            break;
        }
        case "update": {
            autoUpdate(result);
            break;
        }

        default : {
            alert("没有对应类型");
            break;
        }
    }
}


function submitAjaxPage(url, type, dataType, data, target, targetType) {
    var page = "";
    $.ajax({
        url: url,
        type: type, //GET
        async: false,    //或false,是否异步
        data: data,
        timeout: 5000,    //超时时间
        dataType: dataType,    //返回的数据格式：json/xml/html/script/jsonp/text
        success: function (result) {
            page = result.page;
            autoFillData(result, target, targetType);

        },
        error: function (xhr, textStatus) {
            console.log('错误')
            console.log(xhr)
            console.log(textStatus)
        },
        complete: function () {
            console.log('结束')
        }
    });
    return page;
}


function submitAjax(url, type, dataType, data, target, targetType) {
    $.ajax({
        url: url,
        type: type, //GET
        async: true,    //或false,是否异步
        data: data,
        timeout: 5000,    //超时时间
        dataType: dataType,    //返回的数据格式：json/xml/html/script/jsonp/text
        success: function (result) {
            autoFillData(result, target, targetType);
        },
        error: function (xhr, textStatus) {
            console.log('错误')
            console.log(xhr)
            console.log(textStatus)
        },
        complete: function () {
            console.log('结束')
        }
    });
}




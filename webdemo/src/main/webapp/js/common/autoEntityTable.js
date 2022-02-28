



function createTableTitle(tableID,array) {

    $("#" + tableID).empty();
    var line = "<tr>";
    for ( i=0;i<array.length;i++) {
        line = line + "<th>" + array[i][0] + "<th>";
    }
    line=line+"</tr>";
    $("#" + tableID).append(line);

}


function createTableData(tableID,result) {

    if(result.length>0){
        $.each(result,function (i,map) {
            var line="<tr>";
            $.each(map,function(key,value){
                if(value==null || value=="null"){
                    value="";
                }
                line=line+"<td>"+value+"</td>";
            });

            $("#"+tableID).append(line);

        });

    }
}

function loadTable(url,data,tableID) {
    $.ajax({url:url,
        type:"post", //GET
        async:true,    //或false,是否异步
        data:data,
        timeout:5000,    //超时时间
        dataType:'json',    //返回的数据格式：json/xml/html/script/jsonp/text
        success:function(result){
            if(result!=null){
                createTableTitle(tableID);
                createTableData(tableID,result.list);
            }else{
                alert("没有数据");
            }
        }
    });

}
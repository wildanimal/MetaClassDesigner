<#if Exp.isTrue(workflow)>

function download(id) {
  $open('../service/Doc/download?id='
      + id + '&ver=' + $('#' + id + '_ver').val() );
}

$.SGridTypes['file_op'] = {
  renderer : function(cfg) { 
	  $.SGridCellStyle(cfg);

    cfg.cellNode.innerHTML = '<img src="../img/home/download.gif" '
      + 'title="下载文档" onclick="download(' + cfg.rowData.id + ')" />';
  }
};

$.SGridTypes['file_ver'] = {
  renderer : function(cfg) { 
	  $.SGridCellStyle(cfg);
    var html = '<select id="' + cfg.rowData.id + '_ver">';
    for (var i = cfg.rowData.ver; i >= 1; i--) {
      html += '\n<option value=' + i + '>' + i + '</option>';
    }
    html += '</select>';
    cfg.cellNode.innerHTML = html;
  }
};

</#if>

$(document).ready(function() {
	
});
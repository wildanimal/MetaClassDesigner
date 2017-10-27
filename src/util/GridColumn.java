package util;


public class GridColumn
{
	public String name = "";
	public String label = "";
	
	public String align = "";
	public String parent = "";
	public String orderBy = "";
	public String expression = "";
	
	public String width = "";
	public String height = "";
	public String htmlStyle = "";
	public Boolean showFullContent = false;
	public Boolean autoMerge = false;
	public Boolean schedule = false;
	
	// 这部分是kggrid的
	public String url = "";
	public Boolean distinct = false;

	// 下面这些是jqgrid的
	public String formatter = "";
	public String formatoptions = "";
	public Boolean hidden = false;
	public String index = "";
	public Boolean search = false;
	public Boolean sortable = false;
	public Boolean editable = false;
	public Boolean nosort = false;
	
	public String edittype = "";
	public String editrules = "";
	public String editoptions = "";
	public String formoptions = "";
	
	public String fieldType = "";

	public String getAlign() {
		return align;
	}

	public String getParent() {
		return parent;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public String getExpression() {
		return expression;
	}

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}

	public String getHtmlStyle() {
		return htmlStyle;
	}

	public Boolean getShowFullContent() {
		return showFullContent;
	}

	public Boolean getAutoMerge() {
		return autoMerge;
	}

	public Boolean getSchedule() {
		return schedule;
	}

	public String getUrl() {
		return url;
	}

	public Boolean getDistinct() {
		return distinct;
	}

	public String getFormatter() {
		return formatter;
	}

	public String getFormatoptions() {
		return formatoptions;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public String getIndex() {
		return index;
	}

	public Boolean getSearch() {
		return search;
	}

	public Boolean getSortable() {
		return sortable;
	}

	public Boolean getEditable() {
		return editable;
	}

	public Boolean getNosort() {
		return nosort;
	}

	public String getEdittype() {
		return edittype;
	}

	public String getEditrules() {
		return editrules;
	}

	public String getEditoptions() {
		return editoptions;
	}

	public String getFormoptions() {
		return formoptions;
	}

	public String getFieldType() {
		return fieldType;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}
}

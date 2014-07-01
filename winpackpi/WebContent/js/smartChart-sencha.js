
Ext.require([ 'Ext.Window',
              'Ext.form.*',
              'Ext.data.*',
              'Ext.chart.*',
              'Ext.grid.*',
              'Ext.layout.container.Column',
              'Ext.fx.target.Sprite',
              'Ext.layout.container.Fit' ]);

swReportType = {
	CHART : '1',
	MATRIX : '2',
	TABLE : '3'
};
	
swChartType = {
	LINE : "line",
	AREA : "area",
	BAR : "bar",
	COLUMN : "column",
	PIE : "pie",
	GAUGE : "gauge",
	RADAR : "radar",
	SCATTER : "scatter",
	DEFAULT : this.LINE
};

function ReportInfo() 
{
		this.reportType = swReportType.CHART;
		this.chartType = swChartType.DEFAULT;
		this.is3Dimension = true;
		this.isStacked = false;
		this.isChartView = true;
		this.isShowLegend = true;
//		this.stringLabelRotation = 'auto';
		this.stringLabelRotation = null;
		this.target = null;
		this.width = 1024/2;
		this.height = 768/2;
		this.columnSpans =  1;
		this.title = null;
		this.xFieldName = null;
		this.yValueName = null;
		this.xGroupName = null;
		this.yGroupName = null;
		this.groupNames = null;
		this.chart2FieldNames = null;
		this.chart2Type = null;
		this.y2FieldName = null;
		this.values = null;
		this.labelRotate = null;	
};

swReportInfo = new ReportInfo();

swReportResizing = false;

function isEmpty(str) {
    return (!str || 0 === str.length || 'null' === str);
};

Ext.onReady(function () {

	smartChart = {
		labelFont : '11px dotum,Helvetica,sans-serif',
		reportInfos : {},
	
		getFields : function(target) {
			if(!isEmpty(target)){
				swReportInfo = smartChart.reportInfos[target];
			}
			var fields = new Array();
			if(!isEmpty(swReportInfo.xFieldName)){
				fields.push({name: swReportInfo.xFieldName});
				if(!isEmpty(swReportInfo.xGroupName))
					fields.push({name: swReportInfo.xGroupName});
				if(!isEmpty(swReportInfo.yGroupName))
					fields.push({name: swReportInfo.yGroupName});
				if(!isEmpty(swReportInfo.chart2FieldNames)){
					for(var i=0; i<swReportInfo.chart2FieldNames.length; i++)
						fields.push({name: swReportInfo.chart2FieldNames[i]});
				}
			}
			for ( var i = 0; i < swReportInfo.groupNames.length; i++)
				fields.push({name: swReportInfo.groupNames[i]});
			return fields;
		},
		
		getTheme : function(chartType, target){
			if(!isEmpty(target)){
				swReportInfo = smartChart.reportInfos[target];
			}
			if(chartType === swChartType.LINE)
				return "Base";
			else if(chartType === swChartType.AREA)
				return "Base";
			else if(chartType === swChartType.BAR)
				return "Base";
			else if(chartType === swChartType.PIE)
				return "Category2";
			else if(chartType === swChartType.COLUMN)
				return "Base";
			else if(chartType === swChartType.GUAGE)
				return "Base";
			else if(chartType === swChartType.RADAR)
				return "Category2";
			else if(chartType === swChartType.SCATTER)
				return "Base";
		},
		
		getAxes : function(chartType, target) {
			if(!isEmpty(target)){
				swReportInfo = smartChart.reportInfos[target];
			}
			var yAxisPosition = "left";
			var y2AxisPosition = "right";
			var xAxisPosition = "bottom";
			var yAxisGrid = true;
			if(chartType === swChartType.AREA)
				yAxisGrid = false;
			if(chartType === swChartType.BAR){
				yAxisPosition = "bottom";
				y2AxisPosition = "top";
				xAxisPosition = "left";
			}
			
			var xAxisTitle = swReportInfo.xfieldName;
			if(!isEmpty(swReportInfo.title)){
				xAxisTitle = swReportInfo.title;
			}
			
			var groupNames = new Array();
			var count = 0;
			for(var idx=0; idx<swReportInfo.groupNames.length; idx++){
				if(!isEmpty(swReportInfo.y2FieldName) && swReportInfo.groupNames[idx] == swReportInfo.y2FieldName )
					continue;
				groupNames[count++] = swReportInfo.groupNames[idx];
			}
			if(!isEmpty(swReportInfo.chart2FieldNames)){
				for(var i=0; i<swReportInfo.chart2FieldNames.length; i++)
					groupNames.push(swReportInfo.chart2FieldNames[i]);
			}
			if(chartType === swChartType.BAR
					|| chartType === swChartType.PIE
					|| chartType === swChartType.GAUGE
					|| chartType === swChartType.RADAR){
				swReportInfo.labelRotate = {
					font: smartChart.labelFont
				};
			}
			
			var numericLabel = {
					renderer: Ext.util.Format.numberRenderer('0,0'),
					font: smartChart.labelFont
				};
			var stringLabel = {
					font: smartChart.labelFont
				};

			if(chartType === swChartType.PIE) return [];
			else if(chartType === swChartType.RADAR){
				return [{
	                type: 'Radial',
	                position: 'radial',
	                label: {
	                    display: true,
	                    font: smartChart.labelFont
	                }
				}];		
			}else if(chartType === swChartType.GAUGE){
				return [{
	                type: 'gauge',
	                position: 'gauge',
	                title: xAxisTitle,
	                minimum: 0,
	                maximum: 100,
	                steps: 10,
	                margin: -10
	            }];
			}else if(chartType === swChartType.SCATTER){
				return [{
					        type: 'Numeric',
					        position: 'left',
					        fields: groupNames,
					        title: swReportInfo.yValueName,
					        grid: true,
					        minimum: 0,
					        label : numericLabel
					    }, {
					        type: 'Category',
					        position: 'bottom',
					        fields: [ swReportInfo.xFieldName ],
					        title: xAxisTitle,
					        label: swReportInfo.labelRotate
					    }];
			}else if(chartType === swChartType.LINE 
					|| chartType === swChartType.AREA
					|| chartType === swChartType.BAR
					|| chartType === swChartType.COLUMN){
				if(isEmpty(swReportInfo.y2FieldName)){
					return [{
						type : 'Numeric',
						minimum : 0,
						position : yAxisPosition,
						grid : yAxisGrid,
						fields : groupNames,
						minorTickSteps : 1,
						label: numericLabel
					}, {
						type : 'Category',
						position : xAxisPosition,
						fields : [ swReportInfo.xFieldName ],
						title : xAxisTitle,
						label: swReportInfo.labelRotate
					} ];
				}else{
					return [{
						type : 'Numeric',
						minimum : 0,
						position : yAxisPosition,
						grid : yAxisGrid,
						fields : groupNames,
						minorTickSteps : 1,
						label: numericLabel
					},{
						type : 'Numeric',
						minimum : 0,
						position : y2AxisPosition,
						grid : false,
						fields : swReportInfo.chart2FieldNames,
						minorTickSteps : 1,
						label: numericLabel
					}, {
						type : 'Category',
						position : xAxisPosition,
						fields : [ swReportInfo.xFieldName ],
						title : xAxisTitle,
						label: swReportInfo.labelRotate
					} ];

				}
			}
		},
	
		getSeriesForPIE : function(index, target){
			if(!isEmpty(target)){
				swReportInfo = smartChart.reportInfos[target];
			}
			var series = new Array();
			series = [{
			    type: swReportInfo.chartType,
			    field: swReportInfo.groupNames[index],
			    showInLegend: true,
			    donut: 20,
			    highlight: {
			      segment: {
			        margin: 20
			      }
			    },
                tips: {
                    trackMouse: true,
                    height : 32,
                    width : 100,
                    renderer: function(storeItem, item) {
                    	var total = 0;
                    	for(var i=0; i<swReportInfo.values.length; i++){
                    		total += swReportInfo.values[i][ swReportInfo.groupNames[index]];
                    	}
                    	this.setTitle(storeItem.data[ swReportInfo.xFieldName] + "<br>" + storeItem.data[swReportInfo.groupNames[index]] + "  (" + Math.round(storeItem.data[swReportInfo.groupNames[index]]/total * 100) + "%)");
                    }
                },
			    label: {
			        field: swReportInfo.xFieldName,
			        display: 'rotate',
			        contrast: true,
			        font: smartChart.labelFont
			    }		}];
		    return series;
		},
		
		getSeries : function(chartType, target) {			
			if(!isEmpty(target)){
				swReportInfo = smartChart.reportInfos[target];
			}
			var markerConfig = {
					type: 'circle',
					radius: 3,
					size: 3,							
				}; 
			var markerConfigNone = {
					type: 'circle',
					radius: 0,
					size: 0,							
				}; 
			var highlight = {
                    size: 7,
                    radius: 7
                };
			var axis = "left";
			if(chartType === swChartType.BAR) axis = "bottom";

			var y2AxisPosition = "right";
			if(chartType === swChartType.BAR){
				y2AxisPosition = "top";
			}
			if(isEmpty(swReportInfo.y2FieldName)){
				y2AxisPosition = axis;
			}
			
			if(chartType === swChartType.LINE){
				var series = new Array();
				for(var i=0; i<swReportInfo.groupNames.length; i++){
					series.push({
						type : chartType,
						axis : axis,
						xField : swReportInfo.xFieldName,
						yField : swReportInfo.groupNames[i],
						showInLegend: swReportInfo.is3Dimension,
		                highlight: highlight,
		                markerConfig: markerConfig,
		                style:{
							'stroke-width': 0		                	
		                },
		                tips: {
		                    trackMouse: true,
		                    height : 32,
		                    width : 100,
		                    renderer: function(storeItem, item) {
		                    	this.setTitle(item.series.yField + "<br>" + Ext.util.Format.number(item.value[1], (item.value[1]%1==0?'0,000':'0,000.00')));
		                    }
		                }
					});
				}
				if(!isEmpty(swReportInfo.chart2FieldNames) && !isEmpty(swReportInfo.chart2Type)){
					for(var i=0; i<swReportInfo.chart2FieldNames.length; i++)
						if(swReportInfo.chart2Type == swChartType.LINE){
							series.push({
								type : swReportInfo.chart2Type,
								axis : y2AxisPosition,
								xField : swReportInfo.xFieldName,
								yField : swReportInfo.chart2FieldNames[i],
								showInLegend: swReportInfo.is3Dimension,
				                markerConfig: markerConfigNone,
				                highlight: highlight,
				                smooth: true,
				                tips: {
				                    trackMouse: true,
				                    height : 32,
				                    width : 100,
				                    renderer: function(storeItem, item) {
				                    	this.setTitle(item.series.yField + "<br>" + Ext.util.Format.number(item.value[1], (item.value[1]%1==0?'0,000':'0,000.00')));
				                    }
				                }
							});
						}else if(swReportInfo.chart2Type == swChartType.AREA){
							series.push({
								type : swReportInfo.chart2Type,
								axis : y2AxisPosition,
								xField : swReportInfo.xFieldName,
								yField : swReportInfo.chart2FieldNames[i],
							    showInLegend: swReportInfo.is3Dimension,
				                markerConfig: markerConfigNone,
								smooth: true,
				                style: {
				                	opacity: 0.2
				                },
				                tips: {
				                    trackMouse: true,
				                    height : 32,
				                    width : 100,
				                    renderer: function(storeItem, item) {
				                    	this.setTitle(item.series.yField + "<br>" + Ext.util.Format.number(storeItem.data[item.series.yField], (storeItem.data[item.series.yField]%1==0?'0,000':'0,000.00')) );
				                    }
				                }
							});						
						}
				}
				return series;
			}else if(chartType === swChartType.RADAR){
				var series = new Array();
				for(var i=0; i<swReportInfo.groupNames.length; i++){
					series.push({
						type : chartType,
						xField : swReportInfo.xFieldName,
						yField : swReportInfo.groupNames[i],
						showInLegend: swReportInfo.is3Dimension,
						showMarkers: true,
						markerConfig: markerConfig,
		                tips: {
		                    trackMouse: true,
		                    height : 32,
		                    width : 100,
		                    renderer: function(storeItem, item) {
		                    	this.setTitle(item.series.yField + "<br>" + Ext.util.Format.number(storeItem.data[item.series.yField], (storeItem.data[item.series.yField]%1==0?'0,000':'0,000.00')) );
		                    }
		                },
						style:{
							'stroke-width': 2,
							fill: 'none'
						}
					});
				}
				return series;
				
			}else if(chartType === swChartType.SCATTER){
				var series = new Array();
				for(var i=0; i<swReportInfo.groupNames.length; i++){
					series.push({
				        type: chartType,
					    showInLegend: swReportInfo.is3Dimension,
		                highlight: highlight,
		                label: {
		                	orientation: swReportInfo.labelOrientation
		                },
		                markerConfig: markerConfig,
		                tips: {
		                    trackMouse: true,
		                    height : 32,
		                    width : 100,
		                    renderer: function(storeItem, item) {
		                    	this.setTitle(item.series.yField + "<br>" + Ext.util.Format.number(storeItem.data[item.series.yField], (storeItem.data[item.series.yField]%1==0?'0,000':'0,000.00')) );
		                    }
		                },
		                style : {
		                    'stroke-width': 0
		                },
				        axis: 'left',
				        xField: swReportInfo.xFieldName,
				        yField: swReportInfo.groupNames[i]
					});
				}
				return series;
				
			}else if(chartType === swChartType.AREA){
				return [{
					type : chartType,
					axis : axis,
					xField : swReportInfo.xFieldName,
					yField : swReportInfo.groupNames,
				    showInLegend: swReportInfo.is3Dimension,
					highlight : false,
	                tips: {
	                    trackMouse: true,
	                    height : 32,
	                    width : 100,
	                    renderer: function(storeItem, item) {
	                    	this.setTitle(item.storeField + "<br>" + Ext.util.Format.number(storeItem.data[item.storeField], (storeItem.data[item.storeField]%1==0?'0,000':'0,000.00')) );
	                    }
	                }
				}];
				
			}else if( chartType === swChartType.GAUGE
					|| chartType === swChartType.COLUMN
					|| chartType === swChartType.BAR){
				var series = new Array();
				series.push({
					type : chartType,
	                gutter: 80,
					axis : axis,
					xField : swReportInfo.xFieldName,
					yField : swReportInfo.groupNames,
				    showInLegend: swReportInfo.is3Dimension,
					highlight : true,
					stacked : swReportInfo.isStacked,
	                tips: {
	                    trackMouse: true,
	                    height : 32,
	                    width : 100,
	                    renderer: function(storeItem, item) {
	                    	this.setTitle(item.yField + "<br>" + Ext.util.Format.number(item.value[1], (item.value[1]%1==0?'0,000':'0,000.00')));
	                    }
	                }
				});
					
				if(!isEmpty(swReportInfo.chart2FieldNames) && !isEmpty(swReportInfo.chart2Type)){
					for(var i=0; i<swReportInfo.chart2FieldNames.length; i++){
						if(swReportInfo.chart2Type == swChartType.LINE){
							series.push({
								type : swReportInfo.chart2Type,
								axis : y2AxisPosition,
								xField : swReportInfo.xFieldName,
								yField : swReportInfo.chart2FieldNames[i],
								showInLegend: swReportInfo.is3Dimension,
				                markerConfig: markerConfigNone,
				                highlight: highlight,
				                smooth: true,
				                tips: {
				                    trackMouse: true,
				                    height : 32,
				                    width : 100,
				                    renderer: function(storeItem, item) {
				                    	this.setTitle(item.series.yField + "<br>" + Ext.util.Format.number(item.value[1], (item.value[1]%1==0?'0,000':'0,000.00')));
				                    }
				                }
							});
						}else if(swReportInfo.chart2Type == swChartType.AREA){
							series.push({
								type : swReportInfo.chart2Type,
								axis : y2AxisPosition,
								xField : swReportInfo.xFieldName,
								yField : swReportInfo.chart2FieldNames[i],
							    showInLegend: swReportInfo.is3Dimension,
				                markerConfig: markerConfigNone,
								highlight : highlight,
				                style: {
				                	opacity: 0.2
				                },
				                tips: {
				                    trackMouse: true,
				                    height : 32,
				                    width : 100,
				                    renderer: function(storeItem, item) {
				                    	this.setTitle(item.series.yField + "<br>" + Ext.util.Format.number(storeItem.data[item.series.yField], (storeItem.data[item.series.yField]%1==0?'0,000':'0,000.00')) );
				                    }
				                }
							});						
						}
					}
				}
				return series;
			}
		},
	
		loadWithData : function(data, chartType, isStacked, target, chart2Fields, chart2Type, y2Field) {
			if(isEmpty(swReportInfo)){
				reportInfo = new ReportInfo();
				smartChart.reportInfos[target] = reportInfo;
				swReportInfo = reportInfo;
			}
			swReportInfo.reportType = swReportType.CHART;
			if(isEmpty(chartType)) chartType = swChartType.DEFAULT;
			swReportInfo.chartType = chartType;
			swReportInfo.isStacked = isStacked;
			swReportInfo.target = target;
			$('#'+target).html('');
			swReportInfo.width = $('#' + target).width()-20;
			if(data){
				swReportInfo.title = data.title;
				swReportInfo.xFieldName = data.xFieldName;
				swReportInfo.yValueName = data.yValueName;
				swReportInfo.xGroupName = data.xGroupName;
				swReportInfo.yGroupName = data.yGroupName;
				swReportInfo.groupNames = data.groupNames;
				swReportInfo.chart2FieldNames = chart2Fields;
				swReportInfo.chart2Type = chart2Type;
				swReportInfo.y2FieldName = y2Field;
				swReportInfo.values = data.values;
				if((swReportInfo.stringLabelRotation === "auto" && (swReportInfo.values.length>12 || swReportInfo.width<600)) || swReportInfo.stringLabelRotation === "rotated" ){
					swReportInfo.labelRotate = {
		                	rotate : {
		                		degrees : 45
		                	},
							font: smartChart.labelFont
		                };
				}else{
					swReportInfo.labelRotate = {
							font: smartChart.labelFont
					};
				}
				if(swReportInfo.reportType === swReportType.CHART){
					smartChart.createChart();
				}
			}
		},
		
		reload : function(chartType, isStacked, isChartView){
			swReportInfo.chartType = chartType;
			swReportInfo.isStacked = isStacked;
			$('#'+swReportInfo.target).html('');
			swReportInfo.width = $('#' + swReportInfo.target).width();
			if((swReportInfo.stringLabelRotation === "auto" && (swReportInfo.values.length>12 || swReportInfo.width<600)) || swReportInfo.stringLabelRotation === "rotated" ){
				swReportInfo.labelRotate = {
	                	rotate : {
	                		degrees : 45
	                	},
						font: smartChart.labelFont
	                };
			}else{
				swReportInfo.labelRotate = {
						font: smartChart.labelFont
				};
			}
			if(swReportInfo.reportType === swReportType.CHART && isChartView){
				smartChart.createChart();
			}
		},
		
		resize : function(target){
			if(isEmpty(target)){
				$('#'+swReportInfo.target).html('');
				swReportInfo.width = $('#' + swReportInfo.target).width()-20;
				if((swReportInfo.stringLabelRotation === "auto" && (swReportInfo.values.length>12 || swReportInfo.width<600)) || swReportInfo.stringLabelRotation === "rotated" ){
					swReportInfo.labelRotate = {
		                	rotate : {
		                		degrees : 45
		                	},
							font: smartChart.labelFont
		                };
				}else{
					swReportInfo.labelRotate = {
							font: smartChart.labelFont
					};
				}
				
				if(swReportInfo.reportType === swReportType.CHART){
					smartChart.createChart();
				}
			}else{
				$('#'+ target).html('');
				swReportInfo = smartChart.reportInfos[target];
				swReportInfo.width = $('#' + target).width();
				if((swReportInfo.stringLabelRotation === "auto" && (swReportInfo.values.length>12 || swReportInfo.width<600)) || swReportInfo.stringLabelRotation === "rotated" ){
					swReportInfo.labelRotate = {
		                	rotate : {
		                		degrees : 45
		                	},
							font: smartChart.labelFont
		                };
				}else{
					swReportInfo.labelRotate = {
							font: smartChart.labelFont
					};
				}
				
				if(swReportInfo.reportType === swReportType.CHART){
					smartChart.createChart();
				}
				
			}
		},
		
		getColumns : function(target){
			if(!isEmpty(target)){
				swReportInfo = smartChart.reportInfos[target];
			}
        	var columns = new Array();
        	columns.push({
        		id: swReportInfo.xFieldName,
        		text: swReportInfo.xFieldName,
        		flex: 1,
        		sortable: false,
        		dataIndex: swReportInfo.xFieldName,
                summaryType: 'count',
                summaryRenderer: function(value, summaryData, dataIndex) {
                	if(value == swReportInfo.values.length){
                		return smartMessage.get("reportGrandTotal") + '(' + value + ')';
                	}else{
                		return smartMessage.get("reportSubTotal") + '(' + value + ')';
                	}
                }        	});
        	for(var i=0; i<swReportInfo.groupNames.length; i++){
	        	columns.push({
	        		text: swReportInfo.groupNames[i],
	        		align: 'right',
	        		sortable: false,
	                summaryType: 'sum',
	        		dataIndex: swReportInfo.groupNames[i]
	        	});		        		
        	}
        	return columns;			
		},
		
		createChart : function(target){
			if(!isEmpty(target)){
				swReportInfo = smartChart.reportInfos[target];
			}
			
			var legendOption= {
					visible : false
			};
			if(swReportInfo.isShowLegend){
				legendOption = {
						position : 'bottom'						
				};
			}

			if(swReportInfo.chartType === swChartType.PIE){
				for(var i=0; i< swReportInfo.groupNames.length; i++)
					Ext.create('Ext.chart.Chart',{						
						width: swReportInfo.height-60,
						height: swReportInfo.height,
						margin: '20 10 20 10' ,
				        html: '<span style="font-weight: bold; font-size: 14px; font-family: dotum,Helvetica,sans-serif;">' + swReportInfo.groupNames[i] + '</span>',
						animate: true,
						renderTo : Ext.get(swReportInfo.target),
						store : Ext.create('Ext.data.JsonStore', {
							fields : smartChart.getFields(target),
							data : swReportInfo.values
						}),
						shadow : true,
						axes : smartChart.getAxes(swReportInfo.chartType, target),
						series : smartChart.getSeriesForPIE(i, target)
				  	});
			}else if(swReportInfo.chartType === swChartType.GAUGE){
				for(var i=0; i<swReportInfo.groupNames.length; i++)
					Ext.create('Ext.chart.Chart', {
						width: 300,
						height: 200,
						minHeight: 300,
						minWidth: 200,
			            style: 'background:#fff',
			            animate: {
			                easing: 'elasticIn',
			                duration: 1000
			            },
			            renderTo: Ext.get(swReportInfo.target),
			            store : Ext.create('Ext.data.JsonStore', {
							fields : smartChart.getFields(target),
							data : swReportInfo.values
			            }),
			            
			            insetPadding: 25,
			            flex: 1,					
			            axes: smartChart.getAxes(swReportInfo.chartType, target),
			            series: [{
			                type: swReportInfo.chartType,
			                field: swReportInfo.groupNames[i],
			                donut: 30,
			                colorSet: ['#F49D10', '#ddd']
			            }]
			 				});
	
			}else if(swReportInfo.chartType === swChartType.SCATTER){
					Ext.create('Ext.chart.Chart', {
						width: swReportInfo.width,
						height: swReportInfo.height,
						animate: true,
						theme: 'Category2',
						resizable: true,
			            style: 'background:#fff',
			            renderTo: Ext.get(swReportInfo.target),
			            store : Ext.create('Ext.data.JsonStore', {
							fields : smartChart.getFields(target),
							data : swReportInfo.values
			            }),
			            
						legend : legendOption,
			            flex: 1,					
			            axes: smartChart.getAxes(swReportInfo.chartType, target),
			            series: smartChart.getSeries(swReportInfo.chartType, target)
					});
	
			}else{
				Ext.create('Ext.chart.Chart',{
					width: swReportInfo.width,
					height: swReportInfo.height,
					animate: true,
					theme: 'Category2',
					resizable: false,
					autoSize: true,
					insetPadding: 20,// radar
					renderTo : Ext.get(swReportInfo.target),
					store : Ext.create('Ext.data.JsonStore', {
						fields : smartChart.getFields(target),
						data : swReportInfo.values
					}),
					shadow : true,
					legend : legendOption,
					axes : smartChart.getAxes(swReportInfo.chartType, target),
					series : smartChart.getSeries(swReportInfo.chartType, target)
				});
			}
			$(".js_work_report_view_page text[text='" + swReportInfo.xFieldName + "']").css("font-size", "13px");
			$(".js_work_report_view_page text[text='" + swReportInfo.yValueName + "']").css("font-size", "13px");
			$(".js_work_report_view_page text[text='" + swReportInfo.y2FieldName + "']").css("font-size", "13px");
			 setTimeout(function(){
				 try{
				 	window.parent.doIframeAutoHeight();
				 }catch(e){}
			}, 100);
		}
	};
});

var targetElement = function(e){
	return (typeof e.target != 'undefined') ? e.target : e.srcElement;
};

var selectMenuItem = function(menuId){
	$('.js_chart_menu_items th').removeClass('current');
	$('.js_chart_menu_items th[menuId="' + menuId + '"]').addClass('current');
};

var currencyFmatter = function(cellvalue, options, rowObject){
//	if(cellvalue==null || cellvalue==0) return '0���';
//	if(cellvalue<1000000) return numberWithCommas(cellvalue) + '���';
//	return numberWithCommas(Math.round(cellvalue/1000000)) + '諛깅�����';
	if(cellvalue==null || cellvalue==0) return 0;
	return numberWithCommas(Math.round(cellvalue/1000000));	
};

var numberFmatter = function(cellvalue, options, rowObject){
	if(cellvalue==null || cellvalue==0) return '0';
	return numberWithCommas(Math.round(cellvalue/1000));
	
};

var numberWithCommas = function(x) {
    x = x.toString();
    var pattern = /(-?\d+)(\d{3})/;
    while (pattern.test(x))
        x = x.replace(pattern, "$1,$2");
    return x;
};

var zeroPad = function(num, numZeros) {
	var n = Math.abs(num);
	var zeros = Math.max(0, numZeros - Math.floor(n).toString().length );
	var zeroString = Math.pow(10,zeros).toString().substr(1);
	if( num < 0 ) {
		zeroString = '-' + zeroString;
	}

	return zeroString+n;
};

try{
	var doIframeAutoHeight = function (){
	    o = document.getElementsByTagName('iframe');

	    for(var i=0;i<o.length;i++){  
	        if (/\bautoHeight\b/.test(o[i].className)){
	            setHeight(o[i]);
	            addEvent(o[i],'load', doIframeAutoHeight);
	        }
	    }
	};

	var setHeight = function(e){
		try{
		    if(e.contentDocument){
	    		e.height = e.contentDocument.body.scrollHeight; //������ 議곗��        
		    } else {
		//        e.height = e.contentWindow.document.body.scrollHeight + 35;
		        e.height = e.contentWindow.document.body.scrollHeight;
		    }
		}catch(e){
	        e.height = e.contentWindow.document.body.scrollHeight;		
		}
	};

	var addEvent = function(obj, evType, fn){
	    if(obj.addEventListener)
	    {
	    obj.addEventListener(evType, fn,false);
	    return true;
	    } else if (obj.attachEvent){
	    var r = obj.attachEvent("on"+evType, fn);
	    return r;
	    } else {
	    return false;
	    }
	};
}catch(e){}
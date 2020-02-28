var myChartObject = document.getElementById('distancesOverTime');

var chart = new Chart(myChartObject, {
	type: 'line',
	data: {
		datasets: [{
        			label: "Distances over time",
        			backgroundColor: 'rgba(251, 85, 85, 0.4)',
        			borderColor: 'red',
        		}]
        	},
        	options: {
        		scales:{
        			yAxes: [{
        				ticks: {
        					stepSize: 50
        				},
        				scaleLabel:{
        					display: true,
        					labelString:'Distance [Unity Meter]'
        				}
        			}],
        			xAxes: [{
        			    ticks:{
        			        maxTicksLimit: 25
        			    },
        				scaleLabel:{
        					display:true,
        					labelString: 'Time [Seconds]'

        				}
        			}]
        		},
        		legend: {
        			labels: {
        				boxWidth:17,
        				fontSize:13

        			}
        		}
        	}

});

function loadFile() {
    var input, file, fr;

    if (typeof window.FileReader !== 'function') {
      alert("The file API isn't supported on this browser yet.");
      return;
    }

    input = document.getElementById('fileinput');
    if (!input) {
      alert("Um, couldn't find the fileinput element.");
    }
    else if (!input.files) {
      alert("This browser doesn't seem to support the `files` property of file inputs.");
    }
    else if (!input.files[0]) {
      alert("Please select a file before clicking 'Load'");
    }
    else {
      file = input.files[0];
      fr = new FileReader();
      fr.onload = receivedText;
      fr.readAsText(file);
    }

    function receivedText(e) {
      let lines = e.target.result;
      var time =0;
      var newArr = JSON.parse(lines);
      for(var i =0; i<newArr.length;i++){
      console.log(newArr[i].distance)
        chart.data.datasets[0].data.push(newArr[i].distance);
        time += newArr[i].time;
        chart.data.labels.push(time);

      }
      chart.update();
      chart.resize();
    }
  }

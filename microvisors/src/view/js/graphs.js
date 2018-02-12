function Graph(paramName, channel, lowerbound, upperbound, color, divClass) {
    var limit = 60 * 1,
    duration = 2000,
    now = new Date(Date.now() - duration),
    dataPeriod = 2 // Number of seconds passing between a value and another.
    
    this.paramName = paramName;
    this.channel = channel;
    var width = $(document).width()/4,
    height = $(document).height()/4;

    var healthParam = {
        value: 0,
        color: color,
        data: d3.range(limit).map(function() {
            return 0
        })
    };

    this.setData = function(data) {
        healthParam.data.push(data)
    }

    var x_scale = d3.time.scale()
        .domain([now - (limit - dataPeriod), now - duration]) // sure? set 2 instead of dataPeriod
        .range([0, width - 40])

    var y_scale = d3.scale.linear()
        .domain([lowerbound, upperbound])
        .range([height, 0])

    var line = d3.svg.line()
        .interpolate('basis')
        .x(function(d, i) {
            return x_scale(now - (limit - 8 - i) * duration)
        })
        .y(function(d) {
            return y_scale(d)
        })

    var svg = d3.select(divClass).append('svg')
        .attr('class', 'chart')
        .attr('width', width)
        .attr('height', height + 50)

    var xAxis = svg.append('g')
        .attr('class', 'x axis')
        .attr('transform', 'translate(50,' + height + ')')
        .call(x_scale.axis = d3.svg.axis().scale(x_scale).orient('bottom'))

    var paths = svg.append('g')

    healthParam.path = paths.append('path')
        .data([healthParam.data])
        .attr('class', name + ' group')
        .style('stroke', healthParam.color)

    // Ticks for determining current value on y
    var ticks = y_scale.ticks();
        ticks.shift();

    svg.selectAll(".c_y_grid")
        .data(ticks)
        .enter().append("path")
        .attr("d", function (d, i) { return "M" + 50 + "," + y_scale(d) + "L" + (width + 10) + "," + y_scale(d); })
        .attr("class", "c_y_grid");

    // Scale on y axis
    var yAxis = d3.svg.axis()
        .scale(y_scale)
        .orient("left");

    svg.append("g")
        .attr("class", "c_y_axis")
        .attr("transform", "translate(" + 50 + ",0)")
        .call(yAxis);

    // Naming the axis
    svg.append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 10)
        .attr("x", -100)
        .attr("class","c_axis_title")
        .text(paramName);

    function tick() {
        now = new Date()

        healthParam.path.attr('d', line)

        // Shift domain
        x_scale.domain([now - (limit - dataPeriod) * duration, now - duration])

        // Slide x-axis left
        xAxis.transition()
            .duration(duration)
            .ease('linear')
            .call(x_scale.axis)

        // Slide paths left
        paths.attr('transform', null)
            .transition()
            .duration(duration)
            .ease('linear')
            .attr('transform', 'translate(' + x_scale(now - (limit - 1) * duration) + ')')
            .each('end', tick)

        // Remove oldest data point
        healthParam.data.shift()
        if (healthParam.data.length < limit -1)
            healthParam.data.push(0)
    }

    tick()
}
"use client"

import { Line, LineChart, ResponsiveContainer, XAxis, YAxis, CartesianGrid, Tooltip } from "recharts"

const data = [
  { month: "T1", revenue: 85 },
  { month: "T2", revenue: 92 },
  { month: "T3", revenue: 78 },
  { month: "T4", revenue: 105 },
  { month: "T5", revenue: 98 },
  { month: "T6", revenue: 110 },
  { month: "T7", revenue: 115 },
  { month: "T8", revenue: 108 },
  { month: "T9", revenue: 122 },
  { month: "T10", revenue: 118 },
  { month: "T11", revenue: 125 },
  { month: "T12", revenue: 135 },
]

export function RevenueChart() {
  return (
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
        <XAxis dataKey="month" className="text-muted-foreground" />
        <YAxis className="text-muted-foreground" />
        <Tooltip
          contentStyle={{
            backgroundColor: "hsl(var(--card))",
            border: "1px solid hsl(var(--border))",
            borderRadius: "8px",
          }}
          formatter={(value) => [`${value}M VND`, "Doanh thu"]}
        />
        <Line
          type="monotone"
          dataKey="revenue"
          stroke="hsl(var(--primary))"
          strokeWidth={3}
          dot={{ fill: "hsl(var(--primary))", strokeWidth: 2, r: 4 }}
          activeDot={{ r: 6, stroke: "hsl(var(--primary))", strokeWidth: 2 }}
        />
      </LineChart>
    </ResponsiveContainer>
  )
}

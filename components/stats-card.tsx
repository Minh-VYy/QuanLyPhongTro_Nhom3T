import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { TrendingUp, TrendingDown, type LucideIcon } from "lucide-react"
import { cn } from "@/lib/utils"

interface StatsCardProps {
  title: string
  value: string
  change: number
  icon: LucideIcon
  description: string
  variant?: "default" | "warning"
}

export function StatsCard({ title, value, change, icon: Icon, description, variant = "default" }: StatsCardProps) {
  const isPositive = change > 0
  const isNegative = change < 0

  return (
    <Card className={cn(variant === "warning" && "border-yellow-200 dark:border-yellow-800")}>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium text-muted-foreground">{title}</CardTitle>
        <Icon className={cn("h-4 w-4", variant === "warning" ? "text-yellow-600" : "text-muted-foreground")} />
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold text-foreground">{value}</div>
        <div className="flex items-center space-x-2 text-xs text-muted-foreground">
          <div className="flex items-center">
            {isPositive && <TrendingUp className="h-3 w-3 text-green-500 mr-1" />}
            {isNegative && <TrendingDown className="h-3 w-3 text-red-500 mr-1" />}
            <span
              className={cn(
                isPositive && "text-green-500",
                isNegative && "text-red-500",
                change === 0 && "text-muted-foreground",
              )}
            >
              {isPositive && "+"}
              {change}%
            </span>
          </div>
          <span>{description}</span>
        </div>
      </CardContent>
    </Card>
  )
}

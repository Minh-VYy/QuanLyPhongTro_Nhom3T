import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Home, AlertTriangle, FileText, DollarSign, Eye } from "lucide-react"
import { cn } from "@/lib/utils"

interface Activity {
  id: number
  type: string
  message: string
  time: string
  status: "pending" | "urgent" | "success" | "info"
}

interface RecentActivityProps {
  activities: Activity[]
}

const getActivityIcon = (type: string) => {
  switch (type) {
    case "new_property":
      return Home
    case "report":
      return AlertTriangle
    case "contract":
      return FileText
    case "payment":
      return DollarSign
    default:
      return Eye
  }
}

const getStatusColor = (status: string) => {
  switch (status) {
    case "urgent":
      return "destructive"
    case "success":
      return "default"
    case "pending":
      return "secondary"
    default:
      return "outline"
  }
}

export function RecentActivity({ activities }: RecentActivityProps) {
  return (
    <ScrollArea className="h-80">
      <div className="space-y-4">
        {activities.map((activity) => {
          const Icon = getActivityIcon(activity.type)
          return (
            <div key={activity.id} className="flex items-start space-x-3 p-3 rounded-lg border border-border">
              <div
                className={cn(
                  "rounded-full p-2",
                  activity.status === "urgent" && "bg-red-100 dark:bg-red-900/20",
                  activity.status === "success" && "bg-green-100 dark:bg-green-900/20",
                  activity.status === "pending" && "bg-yellow-100 dark:bg-yellow-900/20",
                  activity.status === "info" && "bg-blue-100 dark:bg-blue-900/20",
                )}
              >
                <Icon
                  className={cn(
                    "h-4 w-4",
                    activity.status === "urgent" && "text-red-600 dark:text-red-400",
                    activity.status === "success" && "text-green-600 dark:text-green-400",
                    activity.status === "pending" && "text-yellow-600 dark:text-yellow-400",
                    activity.status === "info" && "text-blue-600 dark:text-blue-400",
                  )}
                />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-foreground">{activity.message}</p>
                <div className="flex items-center justify-between mt-1">
                  <p className="text-xs text-muted-foreground">{activity.time}</p>
                  <Badge variant={getStatusColor(activity.status) as any} className="text-xs">
                    {activity.status === "urgent" && "Khẩn cấp"}
                    {activity.status === "success" && "Thành công"}
                    {activity.status === "pending" && "Chờ xử lý"}
                    {activity.status === "info" && "Thông tin"}
                  </Badge>
                </div>
              </div>
            </div>
          )
        })}
        <div className="text-center pt-4">
          <Button variant="outline" size="sm">
            Xem tất cả hoạt động
          </Button>
        </div>
      </div>
    </ScrollArea>
  )
}

"use client"

import { useState } from "react"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { MessageSquare, CheckCircle } from "lucide-react"

interface ReportResponseDialogProps {
  report: any
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (response: string, status: string) => void
}

export function ReportResponseDialog({ report, open, onOpenChange, onSubmit }: ReportResponseDialogProps) {
  const [response, setResponse] = useState("")
  const [status, setStatus] = useState("in_progress")

  const handleSubmit = () => {
    if (!response.trim()) return
    onSubmit(response, status)
    setResponse("")
    setStatus("in_progress")
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle className="flex items-center space-x-2">
            <MessageSquare className="h-5 w-5" />
            <span>Phản hồi báo cáo</span>
          </DialogTitle>
          <DialogDescription>Gửi phản hồi và cập nhật trạng thái xử lý báo cáo</DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* Report Summary */}
          <div className="p-4 bg-muted rounded-lg">
            <h4 className="font-medium">{report.title}</h4>
            <p className="text-sm text-muted-foreground mt-1">Người báo cáo: {report.reporter}</p>
            <p className="text-sm text-muted-foreground">Đối tượng: {report.reportedUser}</p>
          </div>

          {/* Status Selection */}
          <div className="space-y-2">
            <Label htmlFor="status">Trạng thái xử lý</Label>
            <Select value={status} onValueChange={setStatus}>
              <SelectTrigger>
                <SelectValue placeholder="Chọn trạng thái" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="in_progress">Đang xử lý</SelectItem>
                <SelectItem value="resolved">Đã xử lý xong</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Response Text */}
          <div className="space-y-2">
            <Label htmlFor="response">{status === "resolved" ? "Kết quả xử lý *" : "Phản hồi *"}</Label>
            <Textarea
              id="response"
              placeholder={
                status === "resolved"
                  ? "Mô tả kết quả xử lý và các biện pháp đã thực hiện..."
                  : "Nhập phản hồi cho người báo cáo..."
              }
              value={response}
              onChange={(e) => setResponse(e.target.value)}
              rows={6}
            />
          </div>

          {/* Suggested Actions */}
          <div className="p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
            <h4 className="font-medium text-blue-800 dark:text-blue-200 mb-2">Gợi ý xử lý:</h4>
            <ul className="text-sm text-blue-700 dark:text-blue-300 space-y-1">
              <li>• Liên hệ trực tiếp với các bên liên quan</li>
              <li>• Yêu cầu cung cấp thêm bằng chứng nếu cần</li>
              <li>• Áp dụng biện pháp cảnh cáo hoặc khóa tài khoản</li>
              <li>• Gỡ bỏ bài đăng vi phạm nếu có</li>
            </ul>
          </div>

          {/* Actions */}
          <div className="flex justify-end space-x-2 pt-4">
            <Button variant="outline" onClick={() => onOpenChange(false)}>
              Hủy
            </Button>
            <Button onClick={handleSubmit} disabled={!response.trim()}>
              {status === "resolved" ? (
                <>
                  <CheckCircle className="mr-2 h-4 w-4" />
                  Hoàn thành xử lý
                </>
              ) : (
                <>
                  <MessageSquare className="mr-2 h-4 w-4" />
                  Gửi phản hồi
                </>
              )}
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}

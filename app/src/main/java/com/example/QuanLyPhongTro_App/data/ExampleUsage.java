package com.example.QuanLyPhongTro_App.data;

import android.os.AsyncTask;
import android.util.Log;

import com.example.QuanLyPhongTro_App.data.dao.DatPhongDao;
import com.example.QuanLyPhongTro_App.data.dao.PhongDao;
import com.example.QuanLyPhongTro_App.data.dao.YeuCauHoTroDao;
import com.example.QuanLyPhongTro_App.data.model.DatPhong;
import com.example.QuanLyPhongTro_App.data.model.Phong;
import com.example.QuanLyPhongTro_App.data.model.YeuCauHoTro;

import java.sql.Connection;
import java.util.List;

/**
 * VÍ DỤ SỬ DỤNG CÁC DAO CLASS
 * 
 * CÁCH DÙNG TRONG ACTIVITY/FRAGMENT:
 * 
 * 1. Load danh sách phòng:
 *    new LoadPhongTask().execute();
 * 
 * 2. Tìm kiếm phòng:
 *    new SearchPhongTask().execute("keyword", "1000000", "5000000", "Hải Châu");
 * 
 * 3. Load đặt phòng của user:
 *    new LoadDatPhongTask().execute(userId);
 * 
 * 4. Load yêu cầu hỗ trợ:
 *    new LoadYeuCauHoTroTask().execute(userId);
 */
public class ExampleUsage {
    private static final String TAG = "ExampleUsage";

    // ==================== VÍ DỤ 1: LOAD DANH SÁCH PHÒNG ====================
    public static class LoadPhongTask extends AsyncTask<Void, Void, List<Phong>> {
        private OnPhongLoadedListener listener;

        public LoadPhongTask(OnPhongLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<Phong> doInBackground(Void... voids) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                PhongDao dao = new PhongDao();
                return dao.getAllPhongAvailable(conn);
            } catch (Exception e) {
                Log.e(TAG, "Error loading phòng: " + e.getMessage(), e);
                return null;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(List<Phong> phongList) {
            if (listener != null) {
                if (phongList != null && !phongList.isEmpty()) {
                    listener.onPhongLoaded(phongList);
                } else {
                    listener.onPhongLoadFailed("Không có dữ liệu");
                }
            }
        }
    }

    public interface OnPhongLoadedListener {
        void onPhongLoaded(List<Phong> phongList);
        void onPhongLoadFailed(String error);
    }

    // ==================== VÍ DỤ 2: TÌM KIẾM PHÒNG ====================
    public static class SearchPhongTask extends AsyncTask<String, Void, List<Phong>> {
        private OnPhongLoadedListener listener;

        public SearchPhongTask(OnPhongLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<Phong> doInBackground(String... params) {
            // params[0] = keyword
            // params[1] = minPrice (optional)
            // params[2] = maxPrice (optional)
            // params[3] = quanHuyen (optional)
            
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                PhongDao dao = new PhongDao();
                
                String keyword = params.length > 0 ? params[0] : null;
                Long minPrice = params.length > 1 && params[1] != null ? Long.parseLong(params[1]) : null;
                Long maxPrice = params.length > 2 && params[2] != null ? Long.parseLong(params[2]) : null;
                String quanHuyen = params.length > 3 ? params[3] : null;
                
                return dao.searchPhong(conn, keyword, minPrice, maxPrice, quanHuyen);
            } catch (Exception e) {
                Log.e(TAG, "Error searching phòng: " + e.getMessage(), e);
                return null;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(List<Phong> phongList) {
            if (listener != null) {
                if (phongList != null) {
                    listener.onPhongLoaded(phongList);
                } else {
                    listener.onPhongLoadFailed("Lỗi tìm kiếm");
                }
            }
        }
    }

    // ==================== VÍ DỤ 3: LOAD CHI TIẾT PHÒNG ====================
    public static class LoadPhongDetailTask extends AsyncTask<String, Void, Phong> {
        private OnPhongDetailLoadedListener listener;

        public LoadPhongDetailTask(OnPhongDetailLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        protected Phong doInBackground(String... params) {
            String phongId = params[0];
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                PhongDao dao = new PhongDao();
                return dao.getPhongById(conn, phongId);
            } catch (Exception e) {
                Log.e(TAG, "Error loading phòng detail: " + e.getMessage(), e);
                return null;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(Phong phong) {
            if (listener != null) {
                if (phong != null) {
                    listener.onPhongDetailLoaded(phong);
                } else {
                    listener.onPhongDetailLoadFailed("Không tìm thấy phòng");
                }
            }
        }
    }

    public interface OnPhongDetailLoadedListener {
        void onPhongDetailLoaded(Phong phong);
        void onPhongDetailLoadFailed(String error);
    }

    // ==================== VÍ DỤ 4: LOAD ĐẶT PHÒNG ====================
    public static class LoadDatPhongTask extends AsyncTask<String, Void, List<DatPhong>> {
        private OnDatPhongLoadedListener listener;

        public LoadDatPhongTask(OnDatPhongLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<DatPhong> doInBackground(String... params) {
            String nguoiThueId = params[0];
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                DatPhongDao dao = new DatPhongDao();
                return dao.getDatPhongByNguoiThue(conn, nguoiThueId);
            } catch (Exception e) {
                Log.e(TAG, "Error loading đặt phòng: " + e.getMessage(), e);
                return null;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(List<DatPhong> datPhongList) {
            if (listener != null) {
                if (datPhongList != null) {
                    listener.onDatPhongLoaded(datPhongList);
                } else {
                    listener.onDatPhongLoadFailed("Lỗi tải dữ liệu");
                }
            }
        }
    }

    public interface OnDatPhongLoadedListener {
        void onDatPhongLoaded(List<DatPhong> datPhongList);
        void onDatPhongLoadFailed(String error);
    }

    // ==================== VÍ DỤ 5: LOAD YÊU CẦU HỖ TRỢ ====================
    public static class LoadYeuCauHoTroTask extends AsyncTask<String, Void, List<YeuCauHoTro>> {
        private OnYeuCauHoTroLoadedListener listener;

        public LoadYeuCauHoTroTask(OnYeuCauHoTroLoadedListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<YeuCauHoTro> doInBackground(String... params) {
            String nguoiDungId = params[0];
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                YeuCauHoTroDao dao = new YeuCauHoTroDao();
                return dao.getYeuCauByNguoiDung(conn, nguoiDungId);
            } catch (Exception e) {
                Log.e(TAG, "Error loading yêu cầu hỗ trợ: " + e.getMessage(), e);
                return null;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(List<YeuCauHoTro> yeuCauList) {
            if (listener != null) {
                if (yeuCauList != null) {
                    listener.onYeuCauHoTroLoaded(yeuCauList);
                } else {
                    listener.onYeuCauHoTroLoadFailed("Lỗi tải dữ liệu");
                }
            }
        }
    }

    public interface OnYeuCauHoTroLoadedListener {
        void onYeuCauHoTroLoaded(List<YeuCauHoTro> yeuCauList);
        void onYeuCauHoTroLoadFailed(String error);
    }
}

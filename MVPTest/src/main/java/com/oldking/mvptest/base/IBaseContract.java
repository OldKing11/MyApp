package com.oldking.mvptest.base;

/**
 * Created by OldKing on 2018/6/12 0012.
 */

public interface IBaseContract {

    interface IPresenter<T extends IView> {
        void attachView(T view);

        void detachView();
    }

    interface IView {
        /**
         * 显示进度中
         */
        void showLoading();

        /**
         * 隐藏进度
         */
        void hideLoading();

        /**
         * 显示请求成功
         */
        void showSuccess();

        /**
         * 失败重试
         */
        void showFailed();

        /**
         * 显示当前网络不可用
         */
        void showNoNet();
    }
}

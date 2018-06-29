package com.oldking.mvptest.base;

/**
 * Created by OldKing on 2018/6/12 0012.
 */

public class BasePresenter<T extends IBaseContract.IView> implements IBaseContract.IPresenter<T> {
    private T mView;

    @Override
    public void attachView(T view) {
        mView = view;
    }

    @Override
    public void detachView() {
        if (mView != null) {
            mView = null;
        }
    }
}

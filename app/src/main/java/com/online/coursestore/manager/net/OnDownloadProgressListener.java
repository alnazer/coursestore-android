package com.online.coursestore.manager.net;

public interface OnDownloadProgressListener {
    void onAttachmentDownloadedError();

    void onAttachmentDownloadUpdate(float percent, Integer id);
}

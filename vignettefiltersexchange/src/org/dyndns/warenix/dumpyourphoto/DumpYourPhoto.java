package org.dyndns.warenix.dumpyourphoto;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.dyndns.warenix.http.CustomMultiPartEntity;
import org.dyndns.warenix.http.CustomMultiPartEntity.ProgressListener;
import org.dyndns.warenix.http.HTTPPostContent;

public class DumpYourPhoto implements HTTPPostContent {

	/**
	 * http content
	 */
	CustomMultiPartEntity multipartEntity;

	/**
	 * multipary progress listener
	 */

	ProgressListener progressListener;

	File image;

	public DumpYourPhoto(ProgressListener progressListener, File image) {
		this.progressListener = progressListener;
		this.image = image;
	}

	public CustomMultiPartEntity getMultipartEntity() {
		CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(
				progressListener);
		try {
			multipartContent.addPart("apiKey",
					new StringBody(DYPAPI.API_KEY));
			multipartContent.addPart("files", new FileBody(image));

			return multipartContent;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}

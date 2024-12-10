package com.shopme.common.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);

    public static final String S3_BASE_URI;

    static {
//        String bucketName = System.getenv("AWS_BUCKET_NAME");
        String bucketName = "babaqgaawsbucket";
        String region = System.getenv("AWS_REGION");

        String pattern = "https://%s.s3.%s.amazonaws.com";

        LOGGER.info("VerginiyaShopCommon | Constants | bucketName : " + bucketName);
        LOGGER.info("VerginiyaShopCommon | Constants | region : " + region);

        S3_BASE_URI = bucketName == null ? "" : String.format(pattern, bucketName, region);

        LOGGER.info("VerginiyaShopCommon | S3_BASE_URI | S3_BASE_URI : " + S3_BASE_URI);
    }

}

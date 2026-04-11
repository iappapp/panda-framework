package com.github.iappapp.panda.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReadFolder {
	
	public static List<File> readFlies(String path) {
		File dir = new File(path);
		List<File> folder = new ArrayList<File>();
		listDirectory(dir, folder);
		return folder;
	}

	public static void listDirectory(File dir, List<File> folder) {

		if (!dir.exists()) {
			log.info("dir not exits dir={}", dir.getPath());
		} else {
			if (dir.isFile()) {
				folder.add(dir);
			} else {
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					listDirectory(files[i], folder);
				}
			}
		}
	}
	
}
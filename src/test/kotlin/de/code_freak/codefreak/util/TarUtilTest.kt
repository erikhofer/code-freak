package de.code_freak.codefreak.util

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import java.io.ByteArrayOutputStream

internal class TarUtilTest {

  @Test
  fun `tar is created correctly`() {
    val out = ByteArrayOutputStream()
    TarUtil.createTarFromDirectory(ClassPathResource("util/tar-sample").file, out)
    TarArchiveInputStream(out.toByteArray().inputStream()).use {
      val result = generateSequence { it.nextTarEntry }.map { it.name }.toList()
      assertThat(result, containsInAnyOrder("/", "executable.sh", "foo.txt", "subdir/", "subdir/bar.txt"))
    }
  }

  @Test
  fun `tar persists execute permissions`() {
    val out = ByteArrayOutputStream()
    TarUtil.createTarFromDirectory(ClassPathResource("util/tar-sample").file, out)
    TarArchiveInputStream(out.toByteArray().inputStream()).use {
      val result = generateSequence { it.nextTarEntry }.filter { it.name == "executable.sh" }.first()
      // octal 100744 = int 33252
      assertThat(result.mode, `is`(33252))
    }
  }
}

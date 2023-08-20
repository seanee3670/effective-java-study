package seok.chapter2.item7.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostRepositoryTest {

  @Test
  void cache() throws InterruptedException {
    PostRepository postRepository = new PostRepository();
    CacheKey key1 = new CacheKey(1);
    postRepository.getPostById(key1);

    assertFalse(postRepository.getCache().isEmpty());

    key1 = null;
    // TODO run gc
    System.out.println("run gc");
    System.gc();
    System.out.println("wait");
    Thread.sleep(3000L);

    assertTrue(postRepository.getCache().isEmpty());
  }

//    @Test
//    void backgroundThread() throws InterruptedException {
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        PostRepository postRepository = new PostRepository();
//        CacheKey key1 = new CacheKey(1);
//        postRepository.getPostById(key1);
//
//        Runnable removeOldCache = () -> {
//            System.out.println("running removeOldCache task");
//            Map<CacheKey, Post> cache = postRepository.getCache();
//            Set<CacheKey> cacheKeys = cache.keySet();
//            Optional<CacheKey> key = cacheKeys.stream().min(Comparator.comparing(CacheKey::getCreated));
//            key.ifPresent((k) -> {
//                System.out.println("removing " + k);
//                cache.remove(k);
//            });
//        };
//
//        System.out.println("The time is : " + new Date());
//
//        executor.scheduleAtFixedRate(removeOldCache,
//                1, 3, TimeUnit.SECONDS); // 3초마다 쓰레드가 가장 오래된 캐시 삭제
//
//        Thread.sleep(20000L);
//
//        executor.shutdown();
//    }

}
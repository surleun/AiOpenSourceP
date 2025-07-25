file://<WORKSPACE>/src/main/java/com/sweetspot/server/user/UserService.java
### java.util.NoSuchElementException: next on empty iterator

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
uri: file://<WORKSPACE>/src/main/java/com/sweetspot/server/user/UserService.java
text:
```scala
package com.sweetspot.server.user;


//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import c

@Service
public class UserService {

    private final UserRepository userRepository;

    //@Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 사용자 등록
    @Transactional
    public UserEntity registerUser(UserDTO userDTO) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 전화번호 중복 확인
        if (userRepository.findByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        // UserEntity 생성 후 저장
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));        userEntity.setNickname(userDTO.getNickname());
        userEntity.setPhoneNumber(userDTO.getPhoneNumber());
        userEntity.setPhoneVerified(userDTO.isPhoneVerified());
        userEntity.setCreatedAt(userDTO.getCreatedAt());
        
        return userRepository.save(userEntity);
    }

    // 이메일로 사용자 찾기
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 전화번호로 사용자 찾기
    public UserEntity getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    // 사용자 정보 업데이트
    @Transactional
    public UserEntity updateUser(Long userId, UserDTO userDTO) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPassword(userDTO.getPassword());
        userEntity.setNickname(userDTO.getNickname());
        userEntity.setPhoneNumber(userDTO.getPhoneNumber());
        userEntity.setPhoneVerified(userDTO.isPhoneVerified());
        userEntity.setCreatedAt(userDTO.getCreatedAt());

        return userRepository.save(userEntity);
    }
}

```



#### Error stacktrace:

```
scala.collection.Iterator$$anon$19.next(Iterator.scala:973)
	scala.collection.Iterator$$anon$19.next(Iterator.scala:971)
	scala.collection.mutable.MutationTracker$CheckedIterator.next(MutationTracker.scala:76)
	scala.collection.IterableOps.head(Iterable.scala:222)
	scala.collection.IterableOps.head$(Iterable.scala:222)
	scala.collection.AbstractIterable.head(Iterable.scala:935)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:164)
	dotty.tools.pc.CachingDriver.run(CachingDriver.scala:45)
	dotty.tools.pc.WithCompilationUnit.<init>(WithCompilationUnit.scala:31)
	dotty.tools.pc.SimpleCollector.<init>(PcCollector.scala:351)
	dotty.tools.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector$lzyINIT1(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:88)
	dotty.tools.pc.ScalaPresentationCompiler.semanticTokens$$anonfun$1(ScalaPresentationCompiler.scala:111)
```
#### Short summary: 

java.util.NoSuchElementException: next on empty iterator
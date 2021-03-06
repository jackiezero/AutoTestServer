import groovy.util.logging.Log4j
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

import javax.crypto.spec.SecretKeySpec

import groovy.util.logging.Log4j
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

import javax.crypto.spec.SecretKeySpec
@Log4j
class demo {






        final static String SecretKey = "base64EncodedSecretKey"

        static def header(alg = 'HS256', typ = 'JWT') {
            [alg: alg, typ: typ]
        }
        /**
         * iss (issuer)：签发人
         * exp (expiration time)：过期时间
         * sub (subject)：主题
         * aud (audience)：受众
         * nbf (Not Before)：生效时间
         * iat (Issued At)：签发时间
         * jti (JWT ID)：编号
         * @param playLoad[nowTime , vTime]
         * @param typ
         * @return
         */

        static def create_token(Map claims) {
//        def jws= Jwts.builder().setSubject("Joe").signWith(SignatureAlgorithm.HS256,).compact()

            def header = header()
            def nowMillis = new Date()
            def expireDate = nowMillis + 60 * 60 * 1000 * 1
            def SIGN_KEY = generalSecretKey(SignatureAlgorithm.HS256, SecretKey)
            JwtBuilder builder = Jwts.builder()
                    .setHeader(header)
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS256, SIGN_KEY)
                    .setNotBefore(nowMillis)
                    .setIssuedAt(new Date())
                    .setClaims(claims)
            // 貌似也可以简便的写为signWith(SignatureAlgorithm.HS256,"SECRET".getBytes("UTF-8")【未验证】

//        compact()将它压缩成它的字符串形式。
            String compactJws = builder.compact()
            log.info("用户${claims.userId}生成了token:${compactJws}==================" )
            return compactJws
        }

        static def generalSecretKey(SignatureAlgorithm signatureAlgorithm, String secretKey) {
            // 在Java 8在java.util包下面实现了BASE64编解码API，API简单易懂
            def encodedSecretKey
            try {
                encodedSecretKey = Base64.getEncoder().encode(secretKey.getBytes("utf-8"))
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace()
            }
            // 这样也是可以的 Key key = MacProvider.generateKey()
            if (encodedSecretKey == null) {
                throw new IllegalArgumentException()
            }
            return new SecretKeySpec(encodedSecretKey, signatureAlgorithm.getJcaName())
        }
        static def parseJWT(String compactJws) throws Exception {
            Claims claims =  Jwts.parser()
                    .setSigningKey(SecretKey)
                    .parseClaimsJws(compactJws)
                    .getBody()
            log.info("该token的主人的email，{}",claims.get('userId'))
            return claims
        }



}

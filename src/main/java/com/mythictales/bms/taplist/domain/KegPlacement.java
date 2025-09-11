package com.mythictales.bms.taplist.domain;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(indexes = { @Index(name="idx_kegplacement_active_tap", columnList="tap_id, endedAt") })
public class KegPlacement {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) private Tap tap;
    @ManyToOne(optional=false) private Keg keg;
    @Column(nullable=false) private Instant startedAt = Instant.now();
    private Instant endedAt;
    @Version private long version;
    public KegPlacement(){}
    public KegPlacement(Tap tap, Keg keg){ this.tap=tap; this.keg=keg; }
    public Long getId(){ return id; }
    public Tap getTap(){ return tap; } public void setTap(Tap tap){ this.tap=tap; }
    public Keg getKeg(){ return keg; } public void setKeg(Keg keg){ this.keg=keg; }
    public Instant getStartedAt(){ return startedAt; } public void setStartedAt(Instant t){ this.startedAt=t; }
    public Instant getEndedAt(){ return endedAt; } public void setEndedAt(Instant t){ this.endedAt=t; }
}
